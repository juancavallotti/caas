package com.juancavallotti.tools.caas.api;

import java.lang.String;

public interface ConfigCoordinate {
  String getApplication();

  void setApplication(String application);

  String getVersion();

  void setVersion(String version);

  String getEnvironment();

  void setEnvironment(String environment);
}
