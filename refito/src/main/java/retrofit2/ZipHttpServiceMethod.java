package retrofit2;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

import okhttp3.ResponseBody;
import retrofit2.adapter.rxjava2.RxJava2ZipCallAdapter;

import static retrofit2.Utils.getParameterUpperBound;
import static retrofit2.Utils.getRawType;
import static retrofit2.Utils.hasUnresolvableType;
import static retrofit2.Utils.methodError;

final class ZipHttpServiceMethod extends ServiceMethod<Object> {

  @Override Object invoke(Object[] args) {
    return callAdapter.adapt(new RefitoCall(requestFactory2s, args, callFactory));
  }

  static ZipHttpServiceMethod parseAnnotations(Retrofit retrofit,
      Collection<MethodComposition> chunkMethodCompositions, Type zipMethodType) {
    List<RequestFactory2> requestFactory2s = new ArrayList<>(chunkMethodCompositions.size());
    for (MethodComposition methodInfo : chunkMethodCompositions) {
      Method method = methodInfo.method;
      Type methodReturnType = methodInfo.methodReturnType;
      methodReturnType =
          methodReturnType instanceof ParameterizedType ? getRawType(getParameterUpperBound(0,
              (ParameterizedType) methodReturnType)) : methodReturnType;

      if (methodReturnType == Response.class || methodReturnType == okhttp3.Response.class) {
        throw methodError(method, "'"
            + Utils.getRawType(methodReturnType).getName()
            + "' is not a valid response body type. Did you mean ResponseBody?");
      }
      if (hasUnresolvableType(methodReturnType)) {
        throw methodError(method,
            "Method return type must not include a type variable or wildcard: %s",
            methodReturnType);
      }
      if (methodReturnType == void.class) {
        throw methodError(method, "Service methods cannot return void.");
      }

      RequestFactory requestFactory = RequestFactory.parseAnnotations(retrofit, method);
      if (requestFactory.httpMethod.equals("HEAD") && !Void.class.equals(methodReturnType)) {
        throw methodError(method, "HEAD method must use Void as response type.");
      }

      requestFactory2s.add(
          RequestFactory2.create(
              requestFactory,
              methodInfo.field,
              createResponseConverter(retrofit, method, methodReturnType)
          )
      );
    }

    RxJava2ZipCallAdapter<Object> callAdapter = createCallAdapter(retrofit, zipMethodType);
    return new ZipHttpServiceMethod(requestFactory2s, retrofit.callFactory, callAdapter);
  }

  private static RxJava2ZipCallAdapter<Object> createCallAdapter(
      Retrofit retrofit, Type responseType) {
    //noinspection unchecked
    return (RxJava2ZipCallAdapter<Object>) retrofit.callAdapter(responseType,
        new Annotation[] {new Annotation() {
          @Override
          public Class<? extends Annotation> annotationType() {
            return ZipMethod.class;
          }
        }});
  }

  private static Converter<ResponseBody, Object> createResponseConverter(
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
  private final RxJava2ZipCallAdapter<Object> callAdapter;

  private ZipHttpServiceMethod(List<RequestFactory2> requestFactory2s,
      okhttp3.Call.Factory callFactory, RxJava2ZipCallAdapter<Object> callAdapter) {
    this.requestFactory2s = requestFactory2s;
    this.callFactory = callFactory;
    this.callAdapter = callAdapter;
  }
}
