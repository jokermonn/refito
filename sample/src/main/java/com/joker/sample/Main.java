package com.joker.sample;

import com.joker.sample.api.HomeApi;
import retrofit2.Refito;
import retrofit2.converter.gson.GsonConverterFactory;

public class Main {
  private Refito refito = new Refito.Builder().addConverterFactory(GsonConverterFactory.create()).baseUrl("https://api-m.mtime.cn").build();

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
