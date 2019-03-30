package retrofit2.adapter.rxjava2;

import io.reactivex.Scheduler;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
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
  public CallAdapter<?, ?> get(Type observableType, Annotation[] annotations, Retrofit retrofit) {
    if (findZipMethodAnnotation(annotations)) {
      Class<?> rawObservableType = getRawType(observableType);
      if (rawObservableType == Response.class || rawObservableType == Result.class) {
        throw new IllegalStateException("Response type can not be Response or Result");
      }

      return new RxJava2ZipCallAdapter(observableType, scheduler, isAsync);
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
