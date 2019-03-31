package com.joker.sample.api;

import com.joker.sample.model.Translate;
import com.joker.sample.model.Sample;
import com.joker.sample.model.Search;
import java.io.IOException;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.adapter.rxjava2.Result;
import retrofit2.http.Chunk;
import retrofit2.ZipApi;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.ZipResponseBody;

public interface HomeApi extends ZipApi<HomeApi.HomeApiResponse> {

  String SEARCH = "search";
  String SAMPLE = "sample";
  String FORMAT = "format";
  String LIST = "list";

  @Chunk(SEARCH)
  @GET("/suggest?le=eng&num=80&ver=&doctype=json&keyfrom=&model=&mid=&imei=&vendor=&screen=&ssid=&abtest=")
  HomeApi search(@Query("q") String word);

  @Chunk(SAMPLE)
  @GET("/jsonapi?xmlVersion=5.1&client=&dicts=&keyfrom=&model=&mid=&imei=&vendor=&screen=&ssid=&network=5g&abtest=&jsonversion=2")
  HomeApi sample(@Query("q") String word);

  @Chunk(FORMAT)
  @POST("/jsonapi_s?q=format&le=&t=1554013543375&sign=c11b6ccacefc25ce51b206a1f397c4ec&client=mobile&jsonversion=3&keyversion=20171115&abtest=4&keyfrom=mdict.7.8.9.android&vendor=xiaomi&mid=9&imei=869832047918574&screen=1080x2028&model=MI_8&ssid=%3Cunknown+ssid%3E")
  @FormUrlEncoded
  HomeApi format(@Field("q") String format);

  @Chunk(LIST)
  @GET("/mrm/list?client=android&product=dict&version=7.6.8&abtest=4&rsversion=5&mode=publish&nocache=false")
  HomeApi list();

  @ZipResponseBody class HomeApiResponse {
    @Chunk(SEARCH) private Search search;
    @Chunk(SAMPLE) private Result<Sample> sampleResult;
    @Chunk(FORMAT) private Response<Translate> translateResponse;
    @Chunk(LIST) private ResponseBody listResponseBody;

    public String getMessage() throws IOException {
      return search.getSearch()
          + "\n"
          + sampleResult.response().body().getLevel()
          + "\n"
          + translateResponse.body().getTranslate()
          + "\n"
          + listResponseBody.string();
    }

    public String getSingle() {
      return search.getSearch();
    }
  }
}
