package retrofit2.adapter.rxjava2;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.Single;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import javax.annotation.Nullable;
import retrofit2.CallAdapter;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.ZipMethod;

public final class RxJava2ZipCallAdapterFactory extends CallAdapter.Factory {

  public static RxJava2ZipCallAdapterFactory create() {
    return new RxJava2ZipCallAdapterFactory(null, false);
  }

  public static RxJava2ZipCallAdapterFactory createAsync() {
    return new RxJava2ZipCallAdapterFactory(null, true);
  }

  @SuppressWarnings("ConstantConditions") // Guarding public API nullability.
  public static RxJava2ZipCallAdapterFactory createWithScheduler(Scheduler scheduler) {
    if (scheduler == null) throw new NullPointerException("scheduler == null");
    return new RxJava2ZipCallAdapterFactory(scheduler, false);
  }

  private final @Nullable Scheduler scheduler;
  private final boolean isAsync;

  private RxJava2ZipCallAdapterFactory(@Nullable Scheduler scheduler, boolean isAsync) {
    this.scheduler = scheduler;
    this.isAsync = isAsync;
  }

  @Override
  public CallAdapter<?, ?> get(Type returnType, Annotation[] annotations, Retrofit retrofit) {
    if (findZipMethodAnnotation(annotations)) {
      Class<?> rawType = getRawType(returnType);
      if (rawType == Completable.class) {
        return new RxJava2ZipCallAdapter(Void.class, this.scheduler, this.isAsync, false, false,
            false, true);
      }

      boolean isFlowable = rawType == Flowable.class;
      boolean isSingle = rawType == Single.class;
      boolean isMaybe = rawType == Maybe.class;
      if (rawType != Observable.class && !isFlowable && !isSingle && !isMaybe) {
        throw new IllegalStateException(
            "@ZipMethod not support annotation methods that return " + rawType);
      }

      Type observableType = getParameterUpperBound(0, (ParameterizedType) returnType);
      Class<?> rawObservableType = getRawType(observableType);
      if (rawObservableType == Response.class || rawObservableType == Result.class) {
        throw new IllegalStateException("zip response body type can not be Response or Result");
      }

      return new RxJava2ZipCallAdapter(observableType, scheduler, isAsync, isFlowable, isSingle,
          isMaybe, false);
    }
    return null;
  }

  private boolean findZipMethodAnnotation(Annotation[] annotations) {
    for (Annotation annotation : annotations) {
      if (annotation.annotationType() == ZipMethod.class) {
        return true;
      }
    }
    return false;
  }
}
