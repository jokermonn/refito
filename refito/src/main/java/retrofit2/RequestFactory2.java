package retrofit2;

import io.reactivex.Observable;
import java.lang.reflect.Field;
import okhttp3.ResponseBody;
import retrofit2.http.Chunk;

final class RequestFactory2 {
  private Field field;

  final RequestFactory requestFactory;
  final Converter<ResponseBody, Observable<?>> converter;

  static RequestFactory2 create(RequestFactory requestFactory, String chunkValue,
      Class<?> zipResponseClass, Converter<ResponseBody, Observable<?>> converter) {
    return new RequestFactory2(requestFactory, chunkValue, zipResponseClass, converter);
  }

  private RequestFactory2(RequestFactory requestFactory, String chunkValue, Class<?> zipResponseClass,
      Converter<ResponseBody, Observable<?>> converter) {
    this.requestFactory = requestFactory;
    this.converter = converter;

    for (Field declaredField : zipResponseClass.getDeclaredFields()) {
      Chunk chunk = declaredField.getAnnotation(Chunk.class);
      if (chunk != null && chunk.value().equals(chunkValue)) {
        field = declaredField;
      }
    }
  }

  public Field getField() {
    return field;
  }
}
