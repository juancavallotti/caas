package com.juancavallotti.tools.caas.api;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.lang.String;

@JsonDeserialize(as = DefaultConfigCoordinate.class)
public interface ConfigCoordinate {
  String getApplication();

  void setApplication(String application);

  String getVersion();

  void setVersion(String version);

  String getEnvironment();

  void setEnvironment(String environment);
}
