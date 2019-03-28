package retrofit2;

import io.reactivex.Observable;

public interface ZipApi<T> {
  @ZipMethod Observable<T> zip();
}
