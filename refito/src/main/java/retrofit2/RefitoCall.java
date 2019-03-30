package retrofit2;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import okhttp3.Request;
import retrofit2.adapter.rxjava2.Result;
import retrofit2.http.Chunk;

import static retrofit2.Utils.getParameterUpperBound;
import static retrofit2.Utils.getRawType;

public final class RefitoCall implements Call<Object> {

  private List<RealCall> calls;
  private List<Field> fields;

  public List<RealCall> getCalls() {
    return calls;
  }

  RefitoCall(List<RequestFactory2> requestFactory2s, Object[] args,
      okhttp3.Call.Factory callFactory) {
    calls = new ArrayList<>(requestFactory2s.size());
    fields = new ArrayList<>(requestFactory2s.size());

    for (int i = 0; i < requestFactory2s.size(); i++) {
      RequestFactory2 requestFactory2 = requestFactory2s.get(i);
      Field field = requestFactory2.field;
      calls.add(new RealCall(
          new OkHttpCall<>(requestFactory2.requestFactory, (Object[]) args[i], callFactory,
              requestFactory2.converter),
          field.getGenericType()
      ));
      fields.add(field);
    }
  }

  public void setResponse(int index, Object result, Object object) throws Exception {
    Field filed = fields.get(index);
    filed.setAccessible(true);
    filed.set(result, object);
  }

  public static class RealCall {
    private final Call call;
    private boolean isResult;
    private boolean isBody;

    private RealCall(Call call, Type returnType) {
      this.call = call;

      if (returnType instanceof ParameterizedType) {
        Class<?> rawObservableType = getRawType(returnType);
        if (rawObservableType == Result.class) {
          isResult = true;
        }
      } else {
        isBody = true;
      }
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
  public Call<Object> clone() {
    throw new UnsupportedOperationException();
  }

  @Override public Request request() {
    throw new UnsupportedOperationException();
  }
}
