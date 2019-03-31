package com.joker.sample.model;

import com.google.gson.annotations.SerializedName;

public class SellMovie {
  @SerializedName("count") private int count;

  public SellMovie() {
    count = 13;
  }

  public int getCount() {
    return count;
  }
}
