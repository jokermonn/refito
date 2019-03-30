package retrofit2;

import java.lang.reflect.Field;
import okhttp3.ResponseBody;

final class RequestFactory2 {
  final RequestFactory requestFactory;
  final Converter<ResponseBody, Object> converter;
  final Field field;

  static RequestFactory2 create(RequestFactory requestFactory, Field field,
      Converter<ResponseBody, Object> converter) {
    return new RequestFactory2(requestFactory, field, converter);
  }

  private RequestFactory2(RequestFactory requestFactory, Field field,
      Converter<ResponseBody, Object> converter) {
    this.requestFactory = requestFactory;
    this.converter = converter;
    this.field = field;
  }
}
