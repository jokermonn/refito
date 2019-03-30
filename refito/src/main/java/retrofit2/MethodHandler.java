package retrofit2;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import java.util.Set;
import retrofit2.http.Chunk;
import retrofit2.internal.ChunkValueRepeatException;

final class MethodHandler<T> {

  private final Class<T> zipResponseClass;
  private final Retrofit retrofit;

  MethodHandler(Class<T> zipResponseClass, Retrofit retrofit) {
    this.zipResponseClass = zipResponseClass;
    this.retrofit = retrofit;
  }

  Object handle(Map<Method, Object[]> targetMethods) {
    Field[] declaredFields = zipResponseClass.getDeclaredFields();
    Map<String, Field> chunkWithField = new HashMap<>(declaredFields.length);
    for (Field declaredField : declaredFields) {
      Chunk fieldChunk = declaredField.getAnnotation(Chunk.class);
      if (fieldChunk == null) {
        continue;
      }

      /** Check for duplicate chunk value **/
      String fieldChunkValue = fieldChunk.value();
      if (chunkWithField.containsKey(fieldChunkValue)) {
        throw new ChunkValueRepeatException(fieldChunk);
      }

      chunkWithField.put(fieldChunkValue, declaredField);
    }

    List<MethodComposition> methodCompositions = new ArrayList<>(targetMethods.size());
    List<Object[]> args = new ArrayList<>(targetMethods.size());
    Set<String> methodAnnotationCalibrator = new HashSet<>();
    for (Map.Entry<Method, Object[]> methodEntry : targetMethods.entrySet()) {
      Method method = methodEntry.getKey();

      Chunk methodChunk = method.getAnnotation(Chunk.class);
      if (methodChunk == null) {
        throw new IllegalArgumentException(
            "you must use @Chunk annotated the method " + method);
      }

      /** Check for duplicate chunk value **/
      String methodChunkValue = methodChunk.value();
      if (methodAnnotationCalibrator.contains(methodChunkValue)) {
        throw new ChunkValueRepeatException(methodChunk);
      }
      methodAnnotationCalibrator.add(methodChunkValue);

      Field field = chunkWithField.get(methodChunkValue);
      if (field == null) {
        throw new IllegalArgumentException("do you forget to use "
            + methodChunkValue
            + " to annotation the method response body?");
      }

      methodCompositions.add(MethodComposition.create(method, field));
      args.add(methodEntry.getValue());
    }

    return ZipHttpServiceMethod.parseAnnotations(retrofit, methodCompositions, zipResponseClass)
        .invoke(args.toArray());
  }
}
