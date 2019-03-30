package retrofit2;

import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;

public interface ZipApi<T> {
  @ZipMethod Observable<T> zip();

  @ZipMethod Flowable<T> flowable();

  @ZipMethod Single<T> single();

  @ZipMethod Maybe<T> maybe();
}
