package com.joker.sample;

import com.joker.sample.api.HomeApi;
import okhttp3.OkHttpClient;
import retrofit2.Refito;
import retrofit2.adapter.rxjava2.RxJava2ZipCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class Main {
  private Refito refito = new Refito.Builder()
      .client(new OkHttpClient()
          .newBuilder()
          .build())
      .baseUrl("http://dict.youdao.com")
      .addCallAdapterFactory(RxJava2ZipCallAdapterFactory.create())
      //.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
      .addConverterFactory(GsonConverterFactory.create())
      .build();

  @SuppressWarnings("ResultOfMethodCallIgnored") public static void main(String[] args) {
    Refito refito = new Main().refito;

    refito.create(HomeApi.class)
        .search("hello")
        .sample("world")
        .format("format")
        .list()
        .zip()
        .subscribe(
            response -> System.out.println(response.getMessage()),
            Throwable::printStackTrace
        );

    refito.create(HomeApi.class)
        .search("single")
        .sample("bureaucracy")
        .format("format")
        .list()
        .zip()
        .subscribe(
            response -> System.out.println(response.getMessage()),
            Throwable::printStackTrace
        );

    refito.create(HomeApi.class)
        .search("want")
        .zip()
        .subscribe(
            response -> System.out.println(response.getSingle()),
            Throwable::printStackTrace
        );
  }
}
