package retrofit2;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import okhttp3.Request;
import retrofit2.http.Chunk;

public final class RefitoCall implements Call<Observable<?>> {

  private List<Call> calls;
  private List<Field> fields;

  public List<Call> getCalls() {
    return calls;
  }

  public Field findFiledByIndex(int i) {
    return fields.get(i);
  }

  RefitoCall(List<RequestFactory2> requestFactory2s, Object[] args,
      okhttp3.Call.Factory callFactory) {
    calls = new ArrayList<>(requestFactory2s.size());
    fields = new ArrayList<>(requestFactory2s.size());

    for (int i = 0; i < requestFactory2s.size(); i++) {
      RequestFactory2 requestFactory2 = requestFactory2s.get(i);
      calls.add(new OkHttpCall<>(requestFactory2.requestFactory, (Object[]) args[i], callFactory,
          requestFactory2.converter));
      fields.add(requestFactory2.getField());
    }
  }

  @Override
  public Response<Observable<?>> execute() throws IOException {
    throw new UnsupportedOperationException();
  }

  @Override
  public void enqueue(Callback<Observable<?>> callback) {
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
  public Call<Observable<?>> clone() {
    throw new UnsupportedOperationException();
  }

  @Override public Request request() {
    throw new UnsupportedOperationException();
  }
}
