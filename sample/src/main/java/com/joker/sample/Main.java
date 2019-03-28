package com.joker.sample;

import com.joker.sample.api.HomeApi;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Refito;

public class Main {
  private Refito refito = new Refito.Builder().baseUrl("https://api-m.mtime.cn").build();

  @SuppressWarnings("ResultOfMethodCallIgnored") public static void main(String[] args) {
    Main main = new Main();
    main.refito.create(HomeApi.class)
        .getHot()
        .getSell()
        .zip()
        .subscribe(response -> System.out.println(response.getMessage()),
            Throwable::printStackTrace);
  }
}
