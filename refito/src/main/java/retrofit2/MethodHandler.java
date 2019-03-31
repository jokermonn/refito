package retrofit2;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import retrofit2.http.Chunk;
import retrofit2.internal.ChunkValueRepeatException;

final class MethodHandler {

  final Set<MethodComposition> methodCompositions;
  final List<Object[]> args;

  MethodHandler(Class<?> zipResponseClass, Map<Method, Object[]> methodMap) {
    Field[] zipResponseClassFields = zipResponseClass.getDeclaredFields();
    Map<String, Field> chunkWithField = new HashMap<>(zipResponseClassFields.length);

    for (Field declaredField : zipResponseClassFields) {
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

    methodCompositions = new LinkedHashSet<>(methodMap.size());
    args = new ArrayList<>(methodMap.size());
    Set<String> methodAnnotationCalibrator = new HashSet<>(methodMap.size());
    for (Map.Entry<Method, Object[]> methodEntry : methodMap.entrySet()) {
      Method method = methodEntry.getKey();

      Chunk methodChunk = method.getAnnotation(Chunk.class);
      if (methodChunk == null) {
        throw new IllegalArgumentException(
            "you must use @Chunk annotated the zipMethod " + method);
      }

      /** Check for duplicate chunk value **/
      String methodChunkValue = methodChunk.value();
      if (methodAnnotationCalibrator.contains(methodChunkValue)) {
        throw new ChunkValueRepeatException(methodChunk);
      }
      methodAnnotationCalibrator.add(methodChunkValue);

      Field field = chunkWithField.get(methodChunkValue);
      if (field == null) {
        throw new IllegalArgumentException("do you forget to use \""
            + methodChunkValue
            + "\" to annotation the zipMethod response body?");
      }

      methodCompositions.add(MethodComposition.create(method, field));
      args.add(methodEntry.getValue());
    }
  }
}
