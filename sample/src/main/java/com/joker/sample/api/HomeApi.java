package com.joker.sample.api;

import com.joker.sample.model.HotMovie;
import com.joker.sample.model.SellMovie;
import retrofit2.http.Chunk;
import retrofit2.ZipApi;
import retrofit2.http.GET;
import retrofit2.http.ZipResponseBody;

public interface HomeApi extends ZipApi<HomeApi.Response> {
  @Chunk("sell") @GET("/PageSubArea/HotPlayMovies.api?locationId=290") HomeApi getSell();

  @Chunk("hot") @GET("/Showtime/LocationMovies.api?locationId=290") HomeApi getHot();

  @ZipResponseBody class Response {
    @Chunk("sell") private SellMovie sellMovie;
    @Chunk("hot") private HotMovie hotMovie;

    public String getMessage() {
      return Integer.toString(sellMovie.getCount()) + ", " + hotMovie.getImg();
    }
  }
}
