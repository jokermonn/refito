package retrofit2.adapter.rxjava2;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.functions.Function;
import io.reactivex.plugins.RxJavaPlugins;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.RefitoCall;
import retrofit2.Response;
import retrofit2.UnsafeAllocator;

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
      List<Observable<?>> observableList = new ArrayList<>();
      final List<RefitoCall.RealCall> calls = ((RefitoCall) call).getCalls();
      for (RefitoCall.RealCall realCall : calls) {
        Observable<Response<Object>> responseObservable = isAsync ?
            new CallEnqueueObservable<>(realCall.getCall()) :
            new CallExecuteObservable<>(realCall.getCall());

        Observable<?> observable;
        if (realCall.isResult()) {
          observable = new ResultObservable<>(responseObservable);
        } else if (realCall.isBody()) {
          observable = new BodyObservable<>(responseObservable);
        } else {
          observable = responseObservable;
        }

        observableList.add(observable);
      }
      Observable<?> observable = Observable.zip(observableList, new Function<Object[], Object>() {
        @Override public Object apply(Object[] objects) throws Exception {
          Class<? extends Type> zipResponseType =
              (Class<? extends Type>) RxJava2ZipCallAdapter.this.zipResponseType;
          Object result = UnsafeAllocator.create().newInstance(zipResponseType);
          for (int i = 0; i < objects.length; i++) {
            ((RefitoCall) call).setResponse(i, result, objects[i]);
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
