package retrofit2;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import retrofit2.http.Chunk;
import retrofit2.internal.ChunkValueRepeatException;

final class MethodHandler {
  private final Map<Collection<MethodComposition>, ServiceMethod<?>> serviceMethodCache =
      new ConcurrentHashMap<>();

  private final Class<?> zipResponseClass;
  private final Type zipMethodType;
  private final Retrofit retrofit;

  MethodHandler(Class<?> zipResponseClass, Type zipMethodType, Retrofit retrofit) {
    this.zipResponseClass = zipResponseClass;
    this.zipMethodType = zipMethodType;
    this.retrofit = retrofit;
  }

  Object handle(Map<Method, Object[]> chunkMethods) {
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

    Set<MethodComposition> methodCompositions = new LinkedHashSet<>(chunkMethods.size());
    List<Object[]> args = new ArrayList<>(chunkMethods.size());
    Set<String> methodAnnotationCalibrator = new HashSet<>(chunkMethods.size());
    for (Map.Entry<Method, Object[]> methodEntry : chunkMethods.entrySet()) {
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

    ParameterizedType zipResponseParameterizedType = new ParameterizedType() {
      @Override public Type[] getActualTypeArguments() {
        return new Type[] {zipResponseClass};
      }

      @Override public Type getRawType() {
        return zipMethodType;
      }

      @Override public Type getOwnerType() {
        throw new UnsupportedOperationException();
      }
    };

    return loadServiceMethod(methodCompositions, zipResponseParameterizedType)
        .invoke(args.toArray());
  }

  private ServiceMethod<?> loadServiceMethod(Collection<MethodComposition> methodCompositions,
      Type zipResponseType) {
    ServiceMethod<?> result = serviceMethodCache.get(methodCompositions);
    if (result != null) return result;

    synchronized (serviceMethodCache) {
      result = serviceMethodCache.get(methodCompositions);
      if (result == null) {
        result =
            ZipHttpServiceMethod.parseAnnotations(retrofit, methodCompositions, zipResponseType);
        serviceMethodCache.put(methodCompositions, result);
      }
    }
    return result;
  }
}
