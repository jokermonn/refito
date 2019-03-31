package com.joker.sample.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Search {
  @SerializedName("data") private Model data;

  private static class Model {
    @SerializedName("entries") private List<Model2> model;

    private static class Model2 {
      @SerializedName("explain") private String explain;
    }
  }

  public String getSearch() {
    return data.model.get(0).explain;
  }
}
