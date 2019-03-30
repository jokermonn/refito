package retrofit2;

import io.reactivex.annotations.Nullable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import retrofit2.http.ZipResponseBody;

import static java.util.Collections.unmodifiableList;

public class Refito {
  private Retrofit retrofit;

  Refito(okhttp3.Call.Factory callFactory, HttpUrl baseUrl,
      List<Converter.Factory> converterFactories, List<CallAdapter.Factory> callAdapterFactories,
      @Nullable Executor callbackExecutor, boolean validateEagerly) {
    retrofit = new Retrofit(callFactory, baseUrl, converterFactories, callAdapterFactories,
        callbackExecutor, validateEagerly);
  }

  @SuppressWarnings("unchecked") public <T> T create(final Class<T> service) {
    return ZipApi.class.isAssignableFrom(service) ? (T) Proxy.newProxyInstance(
        service.getClassLoader(), new Class[] {service},
        new InvocationHandler() {
          private final Map<Method, Object[]> methodMap = new HashMap<>();

          public Object invoke(Object proxy, Method method, @Nullable Object[] args)
              throws Throwable {
            if (method.getDeclaringClass() == Object.class) {
              return method.invoke(this, args);
            } else if (method.getDeclaringClass() == ZipApi.class) {
              for (Class<?> declaredClass : service.getDeclaredClasses()) {
                if (declaredClass.getAnnotation(ZipResponseBody.class) != null) {
                  return new MethodHandler(declaredClass, method.getReturnType(), retrofit)
                      .handle(methodMap);
                }
              }
              throw new IllegalArgumentException(
                  "you must use @ZipResponseBody annotated the zip ResponseBody.");
            } else {
              methodMap.put(method, args != null ? args : new Object[0]);
              return proxy;
            }
          }
        }) : retrofit.create(service);
  }

  public static final class Builder {
    private @Nullable okhttp3.Call.Factory callFactory;
    private @Nullable HttpUrl baseUrl;
    private final List<CallAdapter.Factory> callAdapterFactories = new ArrayList<>();
    private final List<Converter.Factory> converterFactories = new ArrayList<>();
    private @Nullable Executor callbackExecutor;
    private boolean validateEagerly;

    public Builder() {
    }

    public Builder client(OkHttpClient client) {
      return callFactory(client);
    }

    public Builder callFactory(okhttp3.Call.Factory factory) {
      this.callFactory = factory;
      return this;
    }

    public Builder baseUrl(String baseUrl) {
      return baseUrl(HttpUrl.get(baseUrl));
    }

    public Builder baseUrl(HttpUrl baseUrl) {
      this.baseUrl = baseUrl;
      return this;
    }

    public Builder addCallAdapterFactory(CallAdapter.Factory factory) {
      callAdapterFactories.add(factory);
      return this;
    }

    public Builder addConverterFactory(Converter.Factory factory) {
      converterFactories.add(factory);
      return this;
    }

    public Builder callbackExecutor(Executor executor) {
      this.callbackExecutor = executor;
      return this;
    }

    public List<Converter.Factory> converterFactories() {
      return this.converterFactories;
    }

    public List<CallAdapter.Factory> callAdapterFactories() {
      return callAdapterFactories;
    }

    public Builder validateEagerly(boolean validateEagerly) {
      this.validateEagerly = validateEagerly;
      return this;
    }

    public Refito build() {
      okhttp3.Call.Factory callFactory = this.callFactory;
      if (callFactory == null) {
        callFactory = new OkHttpClient();
      }

      return new Refito(callFactory, baseUrl, unmodifiableList(converterFactories),
          callAdapterFactories, callbackExecutor, validateEagerly);
    }
  }
}
