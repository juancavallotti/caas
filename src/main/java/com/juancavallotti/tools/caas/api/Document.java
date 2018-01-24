package com.juancavallotti.tools.caas.api;

import java.lang.String;

public interface Document {
  String getKey();

  void setKey(String key);

  String getType();

  void setType(String type);
}
