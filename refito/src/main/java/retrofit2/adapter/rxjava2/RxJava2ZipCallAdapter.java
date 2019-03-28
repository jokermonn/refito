package retrofit2.adapter.rxjava2;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.functions.Function;
import io.reactivex.plugins.RxJavaPlugins;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.RefitoCall;
import retrofit2.RefitoCallResponse;
import retrofit2.http.Chunk;

final class RxJava2ZipCallAdapter<R> implements CallAdapter<R, Object> {

  private final Type responseType;
  private final @Nullable Scheduler scheduler;
  private final boolean isAsync;
  private final boolean isResult;
  private final boolean isBody;

  RxJava2ZipCallAdapter(Type responseType, @Nullable Scheduler scheduler, boolean isAsync,
      boolean isResult, boolean isBody) {
    this.responseType = responseType;
    this.scheduler = scheduler;
    this.isAsync = isAsync;
    this.isResult = isResult;
    this.isBody = isBody;
  }

  @Override public Type responseType() {
    return responseType;
  }

  @SuppressWarnings("unchecked") @Override public Object adapt(Call<R> call) {
    if (call instanceof RefitoCall) {
      List<Observable<RefitoCallResponse>> observableList = new ArrayList<>();
      for (Call realCall : ((RefitoCall) call).getRealCall()) {
        observableList.add(isAsync
            ? new CallEnqueueObservable<>(realCall)
            : new CallExecuteObservable<>(realCall));
      }
      Observable<?> observable = Observable.zip(observableList, new Function<Object[], Object>() {
        @Override public Object apply(Object[] objects) throws Exception {
          Class<? extends Type> response = responseType.getClass();
          Object result = response.newInstance();
          for (Object res : objects) {
            for (Field declaredField : response.getDeclaredFields()) {
              Chunk chunk = declaredField.getAnnotation(Chunk.class);
              if (chunk != null && ((RefitoCallResponse) res).getKey().equals(chunk.value())) {
                declaredField.setAccessible(true);
                declaredField.set(result, declaredField.getType().newInstance());
              }
            }
          }
          return result;
        }
      });
      if (scheduler != null) {
        observable = observable.subscribeOn(scheduler);
      }
      return RxJavaPlugins.onAssembly(observable);
    }
    return null;
  }
}
