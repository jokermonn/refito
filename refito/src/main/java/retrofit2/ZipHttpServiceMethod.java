package retrofit2;

import io.reactivex.Observable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.*;

import okhttp3.ResponseBody;
import retrofit2.http.Chunk;

import static retrofit2.Utils.methodError;

final class ZipHttpServiceMethod extends ServiceMethod<Observable<?>> {

  @Override Observable<?> invoke(Object[] args) {
    return (Observable<?>) callAdapter.adapt(new RefitoCall(requestFactory2s, args, callFactory));
  }

  static ZipHttpServiceMethod parseAnnotations(Retrofit retrofit, List<MethodType> methodTypes,
      Class<?> zipResponseClass) {
    List<RequestFactory2> requestFactory2s = new ArrayList<>(methodTypes.size());
    for (MethodType methodInfo : methodTypes) {
      Method method = methodInfo.method;
      requestFactory2s.add(
          RequestFactory2.create(
              RequestFactory.parseAnnotations(retrofit, method),
              method.getAnnotation(Chunk.class).value(),
              zipResponseClass,
              createResponseConverter(retrofit, method, methodInfo.methodReturnType)
          )
      );
    }

    CallAdapter<Observable<?>, Object> callAdapter = createCallAdapter(retrofit, zipResponseClass);
    return new ZipHttpServiceMethod(requestFactory2s, retrofit.callFactory, callAdapter);
  }

  private static CallAdapter<Observable<?>, Object> createCallAdapter(
      Retrofit retrofit, Type responseType) {
    //noinspection unchecked
    return (CallAdapter<Observable<?>, Object>) retrofit.callAdapter(responseType,
        new Annotation[] {new Annotation() {
          @Override
          public Class<? extends Annotation> annotationType() {
            return ZipMethod.class;
          }
        }});
  }

  private static Converter<ResponseBody, Observable<?>> createResponseConverter(
      Retrofit retrofit, Method method, Type responseType) {
    Annotation[] annotations = method.getAnnotations();
    try {
      return retrofit.responseBodyConverter(responseType, annotations);
    } catch (RuntimeException e) { // Wide exception range because factories are user code.
      throw methodError(method, e, "Unable to create converter for %s", responseType);
    }
  }

  private final okhttp3.Call.Factory callFactory;
  private final List<RequestFactory2> requestFactory2s;
  private final CallAdapter<Observable<?>, Object> callAdapter;

  private ZipHttpServiceMethod(List<RequestFactory2> requestFactory2s,
      okhttp3.Call.Factory callFactory, CallAdapter<Observable<?>, Object> callAdapter) {
    this.requestFactory2s = requestFactory2s;
    this.callFactory = callFactory;
    this.callAdapter = callAdapter;
  }
}
