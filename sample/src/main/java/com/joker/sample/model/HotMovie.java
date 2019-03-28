package com.joker.sample.model;

import com.google.gson.annotations.SerializedName;

public class HotMovie {
  @SerializedName("bImg") private String img;

  public HotMovie() {
    img = "0x111";
  }

  public String getImg() {
    return img;
  }
}
