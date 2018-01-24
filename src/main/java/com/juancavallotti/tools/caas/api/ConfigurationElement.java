package com.juancavallotti.tools.caas.api;

import java.lang.String;
import java.util.List;

public interface ConfigurationElement extends ConfigCoordinate {
  String getApplication();

  void setApplication(String application);

  String getVersion();

  void setVersion(String version);

  String getEnvironment();

  void setEnvironment(String environment);

  List<ConfigCoordinate> getParents();

  void setParents(List<ConfigCoordinate> parents);

  PropertiesType getProperties();

  void setProperties(PropertiesType properties);

  List<Document> getDocuments();

  void setDocuments(List<Document> documents);

  interface PropertiesType {
  }
}
