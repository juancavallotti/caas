package com.juancavallotti.tools.caas.api;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.lang.String;

@JsonDeserialize(as = DefaultDocument.class)
public interface Document {
  String getKey();

  void setKey(String key);

  String getType();

  void setType(String type);
}
