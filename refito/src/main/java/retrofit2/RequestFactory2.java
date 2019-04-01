package retrofit2;

import java.lang.reflect.Field;
import okhttp3.ResponseBody;

final class RequestFactory2 {
  /** used to retrofit **/
  final RequestFactory requestFactory;
  final Converter<ResponseBody, Object> converter;
  /** use to set value **/
  final Field field;
  /** used to determine if the current request is indispensable **/
  final boolean indispensable;

  static RequestFactory2 create(RequestFactory requestFactory, Field field, boolean indispensable,
      Converter<ResponseBody, Object> converter) {
    return new RequestFactory2(requestFactory, field, indispensable, converter);
  }

  private RequestFactory2(RequestFactory requestFactory, Field field, boolean indispensable,
      Converter<ResponseBody, Object> converter) {
    this.requestFactory = requestFactory;
    this.field = field;
    this.indispensable = indispensable;
    this.converter = converter;
  }
}
