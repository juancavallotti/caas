package com.juancavallotti.tools.caas.mongo.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.juancavallotti.tools.caas.api.ConfigCoordinate;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class MongoConfigCoordinate implements ConfigCoordinate {
    private String application;
    private String version;
    private String environment;

    @Override
    public String getApplication() {
        return application;
    }

    @Override
    public void setApplication(String application) {
        this.application = application;
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public String getEnvironment() {
        return environment;
    }

    @Override
    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    protected static MongoConfigCoordinate fromConfigCoordinate(MongoConfigCoordinate target, ConfigCoordinate source) {
        target.setApplication(source.getApplication());
        target.setEnvironment(source.getEnvironment());
        target.setVersion(source.getVersion());
        return target;
    }

    public static MongoConfigCoordinate fromCoordinate(ConfigCoordinate source) {
        return fromConfigCoordinate(new MongoConfigCoordinate(), source);
    }
}
