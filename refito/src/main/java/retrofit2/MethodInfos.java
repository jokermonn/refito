package retrofit2;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

public class MethodInfos {
    private Method method;
    private Object[] args;
    private Type methodReturnType;

    public static MethodInfos create(Method method, Object[] args, Type methodReturnType) {
        return new MethodInfos(method, args, methodReturnType);
    }

    private MethodInfos(Method method, Object[] args, Type methodReturnType) {
        this.method = method;
        this.args = args;
        this.methodReturnType = methodReturnType;
    }

    public Object[] getArgs() {
        return args;
    }

    public Method getMethod() {
        return method;
    }

    public Type getMethodReturnType() {
        return methodReturnType;
    }
}
