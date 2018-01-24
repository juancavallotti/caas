package com.juancavallotti.tools.caas.api;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.lang.String;
import java.util.HashMap;
import java.util.List;

public class DefaultConfigurationElement implements ConfigurationElement {
  private String application;

  private String version;

  private String environment;

  private List<ConfigCoordinate> parents;

  private ConfigurationElement.PropertiesType properties;

  private List<Document> documents;

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

  public List<ConfigCoordinate> getParents() {
    return this.parents;
  }

  public void setParents(List<ConfigCoordinate> parents) {
    this.parents = parents;
  }

  public ConfigurationElement.PropertiesType getProperties() {
    return this.properties;
  }

  public void setProperties(ConfigurationElement.PropertiesType properties) {
    this.properties = properties;
  }

  public List<Document> getDocuments() {
    return this.documents;
  }

  public void setDocuments(List<Document> documents) {
    this.documents = documents;
  }

  public static class DefaultPropertiesType extends HashMap<String, String> implements ConfigurationElement.PropertiesType {
  }
}
