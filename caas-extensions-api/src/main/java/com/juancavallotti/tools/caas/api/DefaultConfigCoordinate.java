package com.juancavallotti.tools.caas.api;

import java.lang.String;
import java.util.Objects;

/**
 * Default pojos should make module developer's life easier.
 */
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || !(o instanceof ConfigCoordinate)) return false;
        ConfigCoordinate that = (ConfigCoordinate) o;
        return Objects.equals(getApplication(), that.getApplication()) &&
                Objects.equals(getVersion(), that.getVersion()) &&
                Objects.equals(getEnvironment(), that.getEnvironment());
    }

    @Override
    public int hashCode() {

        return Objects.hash(application, version, environment);
    }
}
