package retrofit2;

import io.reactivex.annotations.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import retrofit2.adapter.rxjava2.RxJava2ZipCallAdapterFactory;
import retrofit2.http.Chunk;
import retrofit2.internal.ChunkValueRepeatException;
import retrofit2.http.ZipResponseBody;

import static java.util.Collections.unmodifiableList;

public class Refito {
  final okhttp3.Call.Factory callFactory;
  final RxJava2ZipCallAdapterFactory rxJava2ZipCallAdapterFactory;
  Retrofit retrofit;

  Refito(okhttp3.Call.Factory callFactory, HttpUrl baseUrl,
      List<Converter.Factory> converterFactories, List<CallAdapter.Factory> callAdapterFactories,
      @Nullable Executor callbackExecutor, boolean validateEagerly) {
    this.callFactory = callFactory;
    // TODO
    rxJava2ZipCallAdapterFactory = RxJava2ZipCallAdapterFactory.create();
    callAdapterFactories.add(rxJava2ZipCallAdapterFactory);
    retrofit = new Retrofit(callFactory, baseUrl, converterFactories, callAdapterFactories,
        callbackExecutor, validateEagerly);
  }

  @SuppressWarnings("unchecked") public <T> T create(final Class<T> service) {
    return ZipApi.class.isAssignableFrom(service) ? (T) Proxy.newProxyInstance(
        service.getClassLoader(), new Class[] { service },
        new InvocationHandler() {
          private final Map<Method, Object[]> methodMap = new HashMap<>();
          private Set<String> methodAnnotationCalibrator = new HashSet<>();
          private Set<String> fieldAnnotationCalibrator = new HashSet<>();

          private MethodHandler methodHandler;

          {
            for (Class<?> declaredClass : service.getDeclaredClasses()) {
              if (declaredClass.getAnnotation(ZipResponseBody.class) != null) {
                methodHandler = new MethodHandler(declaredClass, retrofit);

                for (Field field : declaredClass.getDeclaredFields()) {
                  Chunk chunk = field.getAnnotation(Chunk.class);
                  if (chunk != null) {
                    String value = chunk.value();
                    if (!fieldAnnotationCalibrator.contains(value)) {
                      fieldAnnotationCalibrator.add(value);
                    } else {
                      throw new ChunkValueRepeatException(chunk);
                    }
                  }
                }
              }
            }
            if (methodHandler == null) {
              throw new IllegalArgumentException(
                  "u must use @ZipResponseBody annotated the ResponseBody.");
            }
          }

          public Object invoke(Object proxy, Method method, @Nullable Object[] args)
              throws Throwable {
            if (method.getDeclaringClass() == Object.class) {
              return method.invoke(this, args);
            } else if (method.getDeclaringClass() == ZipApi.class) {
              methodAnnotationCalibrator.removeAll(fieldAnnotationCalibrator);
              if (methodAnnotationCalibrator.size() > 0) {
                throw new IllegalArgumentException("do u forget to use "
                    + methodAnnotationCalibrator.toString()
                    + " to annotation RefitoCallResponse?");
              }
              return methodHandler.handle(methodMap);
            } else {
              Chunk chunk = method.getAnnotation(Chunk.class);
              if (chunk != null && !methodAnnotationCalibrator.contains(chunk.value())) {
                methodMap.put(method, args != null ? args : new Object[0]);
              } else if (chunk == null) {
                throw new IllegalArgumentException(
                    "u must use @Chunk annotated the method " + method);
              } else {
                throw new ChunkValueRepeatException(chunk);
              }
              methodAnnotationCalibrator.add(chunk.value());

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
      return new Refito(callFactory, baseUrl, unmodifiableList(converterFactories),
          callAdapterFactories, callbackExecutor, validateEagerly);
    }
  }
}
