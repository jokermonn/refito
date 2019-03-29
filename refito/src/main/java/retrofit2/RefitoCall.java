package retrofit2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import okhttp3.Request;

public final class RefitoCall implements Call<Observable<?>> {

  private List<CallWrapper> callWrappers;

  public List<CallWrapper> getCallWrappers() {
    for (RequestFactory2 requestFactory2 : requestFactory2s) {
      callWrappers.add(new CallWrapper(new OkHttpCall(requestFactory2.getRequestFactory(), requestFactory2.getArgs(), callFactory, requestFactory2.getConverter()), requestFactory2.getChunk()));
    }
    return callWrappers;
  }

  private final List<RequestFactory2> requestFactory2s;
  private final okhttp3.Call.Factory callFactory;

  RefitoCall(List<RequestFactory2> requestFactory2s, okhttp3.Call.Factory factory) {
    callWrappers = new ArrayList<>(requestFactory2s.size());
    this.requestFactory2s = requestFactory2s;
    this.callFactory = factory;
  }

  @Override
  public Response<Observable<?>> execute() throws IOException {
    return null;
  }

  @Override
  public void enqueue(Callback<Observable<?>> callback) {

  }

  @Override public boolean isExecuted() {
    return false;
  }

  @Override public void cancel() {

  }

  @Override public boolean isCanceled() {
    return false;
  }

  @Override
  public Call<Observable<?>> clone() {
    return null;
  }


  @Override public Request request() {
    return null;
  }
}
