package retrofit2;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Objects;

final class MethodComposition {
  /** api method **/
  final Method method;
  /** response field **/
  final Field field;
  /** The actual return value of the API method & response filed type **/
  final Type methodReturnType;

  static MethodComposition create(Method method, Field field) {
    return new MethodComposition(method, field);
  }

  private MethodComposition(Method method, Field field) {
    this.method = method;
    this.field = field;
    this.methodReturnType = field.getGenericType();
  }

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    MethodComposition that = (MethodComposition) o;
    return Objects.equals(method, that.method) &&
        Objects.equals(field, that.field) &&
        Objects.equals(methodReturnType, that.methodReturnType);
  }

  @Override public int hashCode() {
    return Objects.hash(method, field, methodReturnType);
  }
}
