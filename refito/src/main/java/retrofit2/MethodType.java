package retrofit2;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

final class MethodType {
  final Method method;
  final Type methodReturnType;

  static MethodType create(Method method, Type methodReturnType) {
    return new MethodType(method, methodReturnType);
  }

  private MethodType(Method method, Type methodReturnType) {
    this.method = method;
    this.methodReturnType = methodReturnType;
  }
}
