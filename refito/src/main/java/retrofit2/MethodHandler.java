package retrofit2;

import io.reactivex.Observable;
import io.reactivex.functions.Function;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import retrofit2.http.Chunk;
import retrofit2.internal.ChunkValueRepeatException;

final class MethodHandler<T> {

  private final Class<T> returnType;
  private final Retrofit retrofit;

  MethodHandler(Class<T> returnType, Retrofit retrofit) {
    this.returnType = returnType;
    this.retrofit = retrofit;
  }

  Object handle(Map<Method, Object[]> targetMethods) {
    try {
      for (Map.Entry<Method, Object[]> entry : targetMethods.entrySet()) {
        Method method = entry.getKey();
      }

      List<Observable<RefitoCallResponse>> results = new ArrayList<>();
      for (Field field : returnType.getDeclaredFields()) {
        Chunk chunk = field.getAnnotation(Chunk.class);
        if (chunk != null) {
          results.add(
              Observable.just(RefitoCallResponse.create(chunk.value(), field.getType().newInstance())));
        }
      }

      return Observable.zip(results, new Function<Object[], T>() {
        @Override public T apply(Object[] responses) throws Exception {
          T result = returnType.newInstance();
          for (Object res : responses) {
            for (Field declaredField : returnType.getDeclaredFields()) {
              Chunk chunk = declaredField.getAnnotation(Chunk.class);
              if (chunk != null && ((RefitoCallResponse) res).getKey().equals(chunk.value())) {
                declaredField.setAccessible(true);
                declaredField.set(result, declaredField.getType().newInstance());
              }
            }
          }
          return result;
        }
      });
    } catch (ChunkValueRepeatException e) {
      throw e;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }
}
