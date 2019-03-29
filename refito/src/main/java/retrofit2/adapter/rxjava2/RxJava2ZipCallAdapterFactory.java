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

public class RxJava2ZipCallAdapterFactory extends CallAdapter.Factory {

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

  private List<CallAdapter> callAdapterList = new ArrayList<>();

  public void setCallAdapters(List<CallAdapter> callAdapters) {
    callAdapterList.clear();
    callAdapterList.addAll(callAdapters);
  }

  @Override
  public CallAdapter<?, ?> get(Type observableType, Annotation[] annotations, Retrofit retrofit) {
    if (findZipMethodAnnotation(annotations)) {
      boolean isResult = false;
      boolean isBody = false;
      Type responseType;

      Class<?> rawObservableType = getRawType(observableType);
      if (rawObservableType == Response.class) {
        if (!(observableType instanceof ParameterizedType)) {
          throw new IllegalStateException("Response must be parameterized"
              + " as Response<Foo> or Response<? extends Foo>");
        }
        responseType = getParameterUpperBound(0, (ParameterizedType) observableType);
      } else if (rawObservableType == Result.class) {
        if (!(observableType instanceof ParameterizedType)) {
          throw new IllegalStateException("Result must be parameterized"
              + " as Result<Foo> or Result<? extends Foo>");
        }
        responseType = getParameterUpperBound(0, (ParameterizedType) observableType);
        isResult = true;
      } else {
        responseType = observableType;
        isBody = true;
      }

      return new RxJava2ZipCallAdapter(responseType, scheduler, isAsync, isResult, isBody, callAdapterList);
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
