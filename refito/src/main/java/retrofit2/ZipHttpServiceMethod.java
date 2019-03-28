package retrofit2;

import io.reactivex.Observable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import okhttp3.ResponseBody;
import retrofit2.http.Chunk;

import static retrofit2.Utils.methodError;

public class ZipHttpServiceMethod extends ServiceMethod<Observable> {
  @Override Observable invoke(Object[] args) {
    return (Observable) callAdapter.adapt(new RefitoCall());
  }

  static ZipHttpServiceMethod parseAnnotations(
      Refito refito, Method[] methods) {
    List<RequestFactory> requestFactories = new ArrayList<>(methods.length);
    for (Method method : methods) {
      requestFactories.add(RequestFactory.parseAnnotations(refito.retrofit, method));
    }
    CallAdapter callAdapter = createCallAdapter(refito, methods[0]);
    Type responseType = callAdapter.responseType();
    Converter responseConverter = createResponseConverter(refito, methods[0], responseType);

    okhttp3.Call.Factory callFactory = refito.callFactory;
    return new ZipHttpServiceMethod(requestFactory, callFactory, callAdapter, responseConverter);
  }

  @SuppressWarnings({"unchecked", "ConstantConditions"})
  private static CallAdapter createCallAdapter(
      Refito refito, Method method) {
    Annotation[] annotations = method.getAnnotations();
    try {
      return refito.rxJava2ZipCallAdapterFactory.get(Observable.class, annotations, null);
    } catch (RuntimeException e) { // Wide exception range because factories are user code.
      throw methodError(method, e, "Unable to create call adapter for %s", Observable.class);
    }
  }

  private static <ResponseT> Converter<ResponseBody, ResponseT> createResponseConverter(
      Refito refito, Method method, Type responseType) {
    Annotation[] annotations = method.getAnnotations();
    try {
      return refito.retrofit.responseBodyConverter(responseType, annotations);
    } catch (RuntimeException e) { // Wide exception range because factories are user code.
      throw methodError(method, e, "Unable to create converter for %s", responseType);
    }
  }

  private final RequestFactory requestFactory;
  private final okhttp3.Call.Factory callFactory;
  private final CallAdapter callAdapter;
  private final Converter responseConverter;

  private ZipHttpServiceMethod(RequestFactory requestFactory, okhttp3.Call.Factory callFactory,
      CallAdapter callAdapter, Converter responseConverter) {
    this.requestFactory = requestFactory;
    this.callFactory = callFactory;
    this.callAdapter = callAdapter;
    this.responseConverter = responseConverter;
  }
}
