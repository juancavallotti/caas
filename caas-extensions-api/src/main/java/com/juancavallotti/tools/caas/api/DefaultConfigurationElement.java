package com.juancavallotti.tools.caas.api;

import java.lang.String;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * Default pojos should make module developer's life easier.
 */
public class DefaultConfigurationElement implements ConfigurationElement {
    private String application;

    private String version;

    private String environment;

    private List<ConfigCoordinate> imports;

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

    @Override
    public List<ConfigCoordinate> getImports() {
        return imports;
    }

    @Override
    public void setImports(List<ConfigCoordinate> imports) {
        this.imports = imports;
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

    @Override
    public boolean equals(Object o) {

        //because configuration coordinates are unique, we dont bother in checking the rest
        //this is useful for in memory implementations, if a framework requires something
        // different, that's why the pojos have interfaces, create your own, or maybe a
        // subclass!
        if (this == o) return true;
        if (o == null || !(o instanceof ConfigCoordinate)) return false;
        ConfigCoordinate that = (ConfigCoordinate) o;
        return Objects.equals(getApplication(), that.getApplication()) &&
                Objects.equals(getVersion(), that.getVersion()) &&
                Objects.equals(getEnvironment(), that.getEnvironment());
    }

    @Override
    public int hashCode() {

        //see equals :D
        return Objects.hash(getApplication(), getVersion(), getEnvironment());
    }
}
