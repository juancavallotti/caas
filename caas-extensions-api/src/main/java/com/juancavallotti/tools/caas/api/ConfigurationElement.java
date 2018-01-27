package com.juancavallotti.tools.caas.api;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.Serializable;
import java.lang.String;
import java.util.List;
import java.util.Map;

@JsonDeserialize(as = DefaultConfigurationElement.class)
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

    @JsonDeserialize(as = DefaultConfigurationElement.DefaultPropertiesType.class)
    interface PropertiesType extends Map<String, String>, Serializable {

    }
}
