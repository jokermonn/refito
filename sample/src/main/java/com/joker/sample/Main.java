package com.joker.sample;

import com.joker.sample.api.HomeApi;
import retrofit2.Refito;
import retrofit2.adapter.rxjava2.RxJava2ZipCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class Main {
  private Refito refito = new Refito.Builder()
      .baseUrl("https://api-m.mtime.cn")
      .addCallAdapterFactory(RxJava2ZipCallAdapterFactory.create())
      //.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
      .addConverterFactory(GsonConverterFactory.create())
      .build();

  @SuppressWarnings("ResultOfMethodCallIgnored") public static void main(String[] args) {
    Main main = new Main();
    main.refito.create(HomeApi.class)
        .getHot("290")
        .getSell("290")
        .getPersons("217896")
        .zip()
        .subscribe(response -> System.out.println(response.getMessage()),
            Throwable::printStackTrace);
  }
}
