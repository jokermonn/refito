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

import static java.util.Collections.replaceAll;
import static java.util.Collections.unmodifiableList;

public class Refito {
  private Retrofit retrofit;

  Refito(Retrofit retrofit) {
    this.retrofit = retrofit;
  }

  @SuppressWarnings("unchecked") public <T> T create(final Class<T> service) {
    return ZipApi.class.isAssignableFrom(service) ? (T) Proxy.newProxyInstance(
        service.getClassLoader(), new Class[] {service},
        new InvocationHandler() {
          private final Map<Method, Object[]> methodMap = new HashMap<>();

          public Object invoke(Object proxy, Method zipMethod, @Nullable Object[] args)
              throws Throwable {
            if (zipMethod.getDeclaringClass() == Object.class) {
              return zipMethod.invoke(this, args);
            } else if (zipMethod.getDeclaringClass() == ZipApi.class) {
              for (Class<?> zipResponseClass : service.getDeclaredClasses()) {
                if (zipResponseClass.getAnnotation(ZipResponseBody.class) != null) {
                  return new MethodHandler(zipResponseClass, zipMethod.getReturnType(), retrofit)
                      .handle(methodMap);
                }
              }
              throw new IllegalArgumentException(
                  "you must use @ZipResponseBody annotated the zip ResponseBody.");
            } else {
              methodMap.put(zipMethod, args != null ? args : new Object[0]);
              return proxy;
            }
          }
        }) : retrofit.create(service);
  }

  public static final class Builder {
    private Retrofit.Builder builder;

    public Builder() {
      builder = new Retrofit.Builder();
    }

    public Builder client(OkHttpClient client) {
      builder.callFactory(client);
      return this;
    }

    public Builder callFactory(okhttp3.Call.Factory factory) {
      builder.callFactory(factory);
      return this;
    }

    public Builder baseUrl(String baseUrl) {
      builder.baseUrl(HttpUrl.get(baseUrl));
      return this;
    }

    public Builder baseUrl(HttpUrl baseUrl) {
      builder.baseUrl(baseUrl);
      return this;
    }

    public Builder addCallAdapterFactory(CallAdapter.Factory factory) {
      builder.addCallAdapterFactory(factory);
      return this;
    }

    public Builder addConverterFactory(Converter.Factory factory) {
      builder.addConverterFactory(factory);
      return this;
    }

    public Builder callbackExecutor(Executor executor) {
      builder.callbackExecutor(executor);
      return this;
    }

    public List<Converter.Factory> converterFactories() {
      return builder.converterFactories();
    }

    public List<CallAdapter.Factory> callAdapterFactories() {
      return builder.callAdapterFactories();
    }

    public Builder validateEagerly(boolean validateEagerly) {
      builder.validateEagerly(validateEagerly);
      return this;
    }

    public Refito build() {
      Retrofit retrofit = builder.build();

      return new Refito(retrofit);
    }
  }
}
