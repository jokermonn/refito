package com.joker.sample;

import com.joker.sample.api.HomeApi;
import java.net.InetSocketAddress;
import java.net.Proxy;
import okhttp3.OkHttpClient;
import retrofit2.Refito;
import retrofit2.adapter.rxjava2.RxJava2ZipCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class Main {
  private Refito refito = new Refito.Builder()
      .client(new OkHttpClient()
          .newBuilder()
          .proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(8888)))
          .build())
      .baseUrl("http://dict.youdao.com")
      .addCallAdapterFactory(RxJava2ZipCallAdapterFactory.create())
      //.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
      .addConverterFactory(GsonConverterFactory.create())
      .build();

  @SuppressWarnings("ResultOfMethodCallIgnored") public static void main(String[] args) {
    Main main = new Main();
    main.refito.create(HomeApi.class)
        .search("hello")
        .sample("world")
        .format("format")
        .list()
        .zip()
        .subscribe(response -> System.out.println(response.getMessage()),
            Throwable::printStackTrace);

    main.refito.create(HomeApi.class).search("single").zip().subscribe(homeApiResponse -> System.out
        .println(homeApiResponse.getSingle()), Throwable::printStackTrace);
  }
}
