package retrofit2;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.adapter.rxjava2.Result;

import static okhttp3.internal.Util.EMPTY_RESPONSE;
import static retrofit2.Utils.getRawType;

public final class RefitoCall implements Call<Object> {

  private List<RealCall> calls;

  public List<RealCall> getCalls() {
    return calls;
  }

  RefitoCall(List<RequestFactory2> requestFactory2s, Object[] args,
      okhttp3.Call.Factory callFactory) {
    calls = new ArrayList<>(requestFactory2s.size());

    for (int i = 0; i < requestFactory2s.size(); i++) {
      RequestFactory2 requestFactory2 = requestFactory2s.get(i);
      Field field = requestFactory2.field;
      calls.add(new RealCall(
          new OkHttpCall<>(
              requestFactory2.requestFactory,
              (Object[]) args[i],
              callFactory,
              requestFactory2.converter
          ),
          field,
          requestFactory2.indispensable
      ));
    }
  }

  public void setResponse(Object instance, Object[] results) throws IllegalAccessException {
    for (int i = 0; i < results.length; i++) {
      Field filed = calls.get(i).field;
      filed.setAccessible(true);
      filed.set(instance, results[i]);
    }
  }

  public static class RealCall {
    private final Call<Object> call;
    private final Field field;
    private final boolean indispensable;
    private boolean isResult;
    private boolean isBody;
    private Object defaultValue;

    private RealCall(Call<Object> call, Field field, boolean indispensable) {
      this.call = call;
      this.field = field;
      this.indispensable = indispensable;

      Type returnType = field.getGenericType();
      if (returnType instanceof ParameterizedType) {
        Class<?> rawObservableType = getRawType(returnType);
        if (rawObservableType == Result.class) {
          isResult = true;
          defaultValue = Result.response(Response.success(null));
        } else if (rawObservableType == Response.class) {
          defaultValue = Response.success(null);
        }
      } else {
        isBody = true;
        if (returnType == ResponseBody.class) {
          defaultValue = EMPTY_RESPONSE;
        } else {
          try {
            defaultValue = UnsafeAllocator.create().newInstance(field.getType());
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      }
    }

    public boolean isIndispensable() {
      return indispensable;
    }

    public Call getCall() {
      return call;
    }

    public boolean isResult() {
      return isResult;
    }

    public boolean isBody() {
      return isBody;
    }

    public Object defaultValue() {
      return defaultValue;
    }
  }

  @Override
  public Response<Object> execute() throws IOException {
    throw new UnsupportedOperationException();
  }

  @Override
  public void enqueue(Callback<Object> callback) {
    throw new UnsupportedOperationException();
  }

  @Override public boolean isExecuted() {
    throw new UnsupportedOperationException();
  }

  @Override public void cancel() {
    throw new UnsupportedOperationException();
  }

  @Override public boolean isCanceled() {
    throw new UnsupportedOperationException();
  }

  @Override
  public RefitoCall clone() {
    throw new UnsupportedOperationException();
  }

  @Override public Request request() {
    throw new UnsupportedOperationException();
  }
}
