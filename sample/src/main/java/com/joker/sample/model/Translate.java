package com.joker.sample.model;

import com.google.gson.annotations.SerializedName;

public class Translate {
  @SerializedName("blng_sents_part") private Model model;

  private static class Model {
    @SerializedName("more") private String more;
  }

  public String getTranslate() {
    return model.more;
  }
}
