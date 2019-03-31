package com.joker.sample.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Sample {
  @SerializedName("exam_dict") private Model model;

  private static class Model {
    @SerializedName("exam_type") private List<String> types;
  }

  public String getLevel() {
    return "单词类属：" + model.types.get(0);
  }
}
