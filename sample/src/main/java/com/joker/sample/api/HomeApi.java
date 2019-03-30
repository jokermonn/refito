package com.joker.sample.api;

import com.joker.sample.model.Image;
import com.joker.sample.model.HotMovie;
import com.joker.sample.model.SellMovie;
import retrofit2.Response;
import retrofit2.adapter.rxjava2.Result;
import retrofit2.http.Chunk;
import retrofit2.ZipApi;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.ZipResponseBody;

public interface HomeApi extends ZipApi<HomeApi.HomeApiResponse> {
  @Chunk("sell") @GET("/PageSubArea/HotPlayMovies.api") HomeApi getSell(
      @Query("locationId") String query);

  @Chunk("hot") @GET("/Showtime/LocationMovies.api") HomeApi getHot(
      @Query("locationId") String query);

  @Chunk("image") @GET("/Movie/ImageAll.api") HomeApi getPersons(@Query("movieId") String movieId);

  @ZipResponseBody class HomeApiResponse {
    @Chunk("sell") private SellMovie sellMovie;
    @Chunk("hot") private Result<HotMovie> hotMovieResult;
    @Chunk("image") private Response<Image> imageResponse;

    public String getMessage() {
      return Integer.toString(sellMovie.getCount())
          + ", "
          + hotMovieResult.response().body().getImg()
          + ", "
          + imageResponse.body().getTypes();
    }
  }
}
