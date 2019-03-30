package retrofit2;

import io.reactivex.Observable;
import java.lang.reflect.Field;
import okhttp3.ResponseBody;

final class RequestFactory2 {
  final RequestFactory requestFactory;
  final Converter<ResponseBody, Observable<?>> converter;
  final Field field;

  static RequestFactory2 create(RequestFactory requestFactory, Field field,
      Converter<ResponseBody, Observable<?>> converter) {
    return new RequestFactory2(requestFactory, field, converter);
  }

  private RequestFactory2(RequestFactory requestFactory, Field field,
      Converter<ResponseBody, Observable<?>> converter) {
    this.requestFactory = requestFactory;
    this.converter = converter;
    this.field = field;
  }
}
