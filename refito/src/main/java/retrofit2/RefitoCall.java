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
    /** {@link OkHttpCall} instance **/
    private final Call<Object> call;
    /** The corresponding field of response **/
    private final Field field;
    /** whether the response is indispensable **/
    private final boolean indispensable;
    /** {@link Result} or {@link ResponseBody} or {@link Response} or not ParameterizedType **/
    private final Type returnType;
    /** returnType is {@link Result} **/
    private boolean isResult;
    /** returnType is not ParameterizedType **/
    private boolean isBody;

    private RealCall(Call<Object> call, Field field, boolean indispensable) {
      this.call = call;
      this.field = field;
      this.indispensable = indispensable;

      returnType = field.getGenericType();
      if (returnType instanceof ParameterizedType) {
        Class<?> rawObservableType = getRawType(returnType);
        if (rawObservableType == Result.class) {
          isResult = true;
        }
      } else {
        isBody = true;
      }
    }

    public boolean isNotIndispensable() {
      return !indispensable;
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

    public Object defaultValue() throws Exception {
      return returnType == ResponseBody.class ? EMPTY_RESPONSE
          : UnsafeAllocator.create().newInstance(field.getType());
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

  private volatile boolean canceled;

  @Override public void cancel() {
    canceled = true;

    for (RealCall call : calls) {
      call.call.cancel();
    }
  }

  @Override public boolean isCanceled() {
    return canceled;
  }

  @Override
  public RefitoCall clone() {
    throw new UnsupportedOperationException();
  }

  @Override public Request request() {
    throw new UnsupportedOperationException();
  }
}
