package retrofit2;

import java.io.IOException;
import java.util.List;
import okhttp3.Request;

public final class RefitoCall<T> implements Call<T> {
  public List<Call> getRealCall() {
    return null;
  }

  @Override public Response<T> execute() throws IOException {
    return null;
  }

  @Override public void enqueue(Callback<T> callback) {

  }

  @Override public boolean isExecuted() {
    return false;
  }

  @Override public void cancel() {

  }

  @Override public boolean isCanceled() {
    return false;
  }

  @Override public Call<T> clone() {
    return null;
  }

  @Override public Request request() {
    return null;
  }
}
