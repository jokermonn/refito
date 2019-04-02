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

  @SuppressWarnings({"unchecked", "ResultOfMethodCallIgnored"}) @Override
  public Object adapt(final Call<R> call) {
    if (call instanceof RefitoCall) {
      List<Observable<?>> observableList = new ArrayList<>();
      final List<RefitoCall.RealCall> calls = ((RefitoCall) call).getCalls();
      for (final RefitoCall.RealCall realCall : calls) {
        Observable<Response<Object>> responseObservable = isAsync ?
            new CallEnqueueObservable<>(realCall.getCall()) :
            new CallExecuteObservable<>(realCall.getCall());

        Observable observable;
        if (realCall.isResult()) {
          observable = new ResultObservable<>(responseObservable);
          if (realCall.isNotIndispensable()) {
            observable = observable
                .onErrorReturn(new Function<Throwable, Result<Object>>() {
                  @Override public Result<Object> apply(Throwable throwable) throws Exception {
                    return Result.response(Response.success(null));
                  }
                });
          }
        } else if (realCall.isBody()) {
          observable = new BodyObservable<>(responseObservable);
          if (realCall.isNotIndispensable()) {
            observable = observable
                .onErrorReturn(new Function() {
                  @Override public Object apply(Object o) throws Exception {
                    return realCall.defaultValue();
                  }
                });
          }
        } else {
          observable = responseObservable;
          if (realCall.isNotIndispensable()) {
            observable = observable
                .onErrorReturn(new Function<Throwable, Response<Object>>() {
                  @Override public Response<Object> apply(Throwable throwable) throws Exception {
                    return Response.success(null);
                  }
                });
          }
        }

        observableList.add(observable);
      }
      Observable<?> observable = Observable.zip(observableList, new Function<Object[], Object>() {
        @Override public Object apply(Object[] objects) throws Exception {
          Class<? extends Type> zipResponseType =
              (Class<? extends Type>) RxJava2ZipCallAdapter.this.zipResponseType;
          Object result = UnsafeAllocator.create().newInstance(zipResponseType);
          ((RefitoCall) call).setResponse(result, objects);
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
