package com.joker.sample.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Image {
  @SerializedName("imageTypes") private List<Type> types;

  public static class Type {
    @SerializedName("typeName") private String typeName;
  }

  public String getTypes() {
    StringBuilder stringBuilder = new StringBuilder();
    for (int i = 0; i < types.size(); i++) {
      stringBuilder.append(types.get(i).typeName).append(", ");
    }
    return stringBuilder.toString();
  }
}
