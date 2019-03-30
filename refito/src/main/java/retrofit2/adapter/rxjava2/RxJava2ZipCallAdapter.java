package retrofit2.adapter.rxjava2;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.functions.Function;
import io.reactivex.plugins.RxJavaPlugins;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;

import retrofit2.*;

public final class RxJava2ZipCallAdapter<R> implements CallAdapter<R, Object> {

  private final Type zipResponseType;
  private final @Nullable Scheduler scheduler;
  private final boolean isAsync;
  private final boolean isFlowable;
  private final boolean isSingle;
  private final boolean isMaybe;
  private final boolean isCompletable;

  RxJava2ZipCallAdapter(Type zipResponseType, @Nullable Scheduler scheduler, boolean isAsync,
      boolean isFlowable, boolean isSingle, boolean isMaybe, boolean isCompletable) {
    this.zipResponseType = zipResponseType;
    this.scheduler = scheduler;
    this.isAsync = isAsync;
    this.isFlowable = isFlowable;
    this.isSingle = isSingle;
    this.isMaybe = isMaybe;
    this.isCompletable = isCompletable;
  }

  @Override public Type responseType() {
    return zipResponseType;
  }

  @SuppressWarnings("unchecked") @Override public Object adapt(final Call<R> call) {
    if (call instanceof RefitoCall) {
      List<Observable<Response>> observableList = new ArrayList<>();
      final List<Call> callWrappers = ((RefitoCall) call).getCalls();
      for (Call realCall : callWrappers) {
        observableList.add(isAsync ? new CallEnqueueObservable<>(realCall)
            : new CallExecuteObservable<>(realCall));
      }
      Observable<?> observable = Observable.zip(observableList, new Function<Object[], Object>() {
        @Override public Object apply(Object[] objects) throws Exception {
          Class<? extends Type> zipResponseType =
              (Class<? extends Type>) RxJava2ZipCallAdapter.this.zipResponseType;
          Object result = zipResponseType.newInstance();
          for (int i = 0; i < objects.length; i++) {
            Response object = (Response) objects[i];
            Field filed = ((RefitoCall) call).findFiledByIndex(i);
            filed.setAccessible(true);
            filed.set(result, object.body());
          }
          return result;
        }
      });

      if (scheduler != null) {
        observable = observable.subscribeOn(scheduler);
      }

      if (isFlowable) {
        return observable.toFlowable(BackpressureStrategy.LATEST);
      }
      if (isSingle) {
        return observable.singleOrError();
      }
      if (isMaybe) {
        return observable.singleElement();
      }
      if (isCompletable) {
        return observable.ignoreElements();
      }
      return RxJavaPlugins.onAssembly(observable);
    }
    throw new IllegalStateException("call must be instance of RefitoCall in RxJava2ZipCallAdapter");
  }
}
