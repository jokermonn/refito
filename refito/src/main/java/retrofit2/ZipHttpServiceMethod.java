package retrofit2;

import io.reactivex.Observable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.*;

import okhttp3.ResponseBody;
import retrofit2.adapter.rxjava2.RxJava2ZipCallAdapterFactory;
import retrofit2.http.Chunk;

import static retrofit2.Utils.methodError;

public class ZipHttpServiceMethod extends ServiceMethod<Observable<?>> {
  @Override Observable<?> invoke(Object[] args) {
    return (Observable<?>) callAdapter.adapt(new RefitoCall(requestFactory2s, callFactory));
  }

  static ZipHttpServiceMethod parseAnnotations(Retrofit retrofit, List<MethodInfos> methodInfos, RxJava2ZipCallAdapterFactory factory, Class<?> responseClass) {
      List<RequestFactory2> requestFactory2s = new ArrayList<>(methodInfos.size());
      List<CallAdapter> callAdapters = new ArrayList<>(methodInfos.size());
      for (MethodInfos methodInfo : methodInfos) {
          Method method = methodInfo.getMethod();
          Converter responseConverter = createResponseConverter(retrofit, method, methodInfo.getMethodReturnType());
          requestFactory2s.add(RequestFactory2.create(RequestFactory.parseAnnotations(retrofit, method), methodInfo.getArgs(), method.getAnnotation(Chunk.class).value(),responseConverter));
      }

    return new ZipHttpServiceMethod(requestFactory2s, callAdapters, retrofit.callFactory, factory, responseClass);
  }

  private static <ResponseT> Converter<ResponseBody, ResponseT> createResponseConverter(
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
  private final CallAdapter callAdapter;

  private ZipHttpServiceMethod(List<RequestFactory2> requestFactory2s, List<CallAdapter> callAdapters, okhttp3.Call.Factory callFactory, RxJava2ZipCallAdapterFactory factory, Class<?> responseClass) {
      this.requestFactory2s = requestFactory2s;
    this.callFactory = callFactory;
    factory.setCallAdapters(callAdapters);
    this.callAdapter = factory.get(responseClass, new Annotation[]{new Annotation() {
        @Override
        public Class<? extends Annotation> annotationType() {
            return ZipMethod.class;
        }
    }}, null);
  }
}
