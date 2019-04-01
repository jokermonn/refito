package retrofit2;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Objects;

final class MethodComposition {
  /** api method **/
  final Method method;
  /** The actual return value of the API method & response filed type **/
  final Type methodReturnType;
  /** for {@link RequestFactory2} **/
  final Field field;
  final boolean indispensable;

  static MethodComposition create(Method method, Field field, boolean indispensable) {
    return new MethodComposition(method, field, indispensable);
  }

  private MethodComposition(Method method, Field field, boolean indispensable) {
    this.method = method;
    this.field = field;
    this.indispensable = indispensable;
    this.methodReturnType = field.getGenericType();
  }

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    MethodComposition that = (MethodComposition) o;
    return indispensable == that.indispensable &&
        Objects.equals(method, that.method) &&
        Objects.equals(methodReturnType, that.methodReturnType) &&
        Objects.equals(field, that.field);
  }

  @Override public int hashCode() {
    return Objects.hash(method, methodReturnType, field, indispensable);
  }
}
