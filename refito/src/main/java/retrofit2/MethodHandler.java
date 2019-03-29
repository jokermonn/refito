package retrofit2;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.adapter.rxjava2.RxJava2ZipCallAdapterFactory;
import retrofit2.http.Chunk;
import retrofit2.internal.ChunkValueRepeatException;

final class MethodHandler<T> {

  private final Class<T> responseClass;
  private final Retrofit retrofit;

  MethodHandler(Class<T> responseType, Retrofit retrofit) {
    this.responseClass = responseType;
    this.retrofit = retrofit;
  }

  Object handle(Map<Method, Object[]> targetMethods, RxJava2ZipCallAdapterFactory factory) {
    try {
      List<MethodInfos> methodInfos = new ArrayList<>(targetMethods.size());
      for (Map.Entry<Method, Object[]> methodEntry : targetMethods.entrySet()) {
        Method method = methodEntry.getKey();
        String chunk = method.getAnnotation(Chunk.class).value();
        for (Field field : responseClass.getDeclaredFields()) {
          Chunk annotation = field.getAnnotation(Chunk.class);
          if (annotation != null && annotation.value().equals(chunk)) {
            methodInfos.add(MethodInfos.create(method, methodEntry.getValue(), field.getType()));
          }
        }
      }
      return ZipHttpServiceMethod.parseAnnotations(retrofit, methodInfos, factory, responseClass).invoke(null);
    } catch (ChunkValueRepeatException e) {
      throw e;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }
}
