package com.juancavallotti.tools.caas.api;

import java.lang.String;

public class DefaultConfigCoordinate implements ConfigCoordinate {
  private String application;

  private String version;

  private String environment;

  public String getApplication() {
    return this.application;
  }

  public void setApplication(String application) {
    this.application = application;
  }

  public String getVersion() {
    return this.version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public String getEnvironment() {
    return this.environment;
  }

  public void setEnvironment(String environment) {
    this.environment = environment;
  }
}
