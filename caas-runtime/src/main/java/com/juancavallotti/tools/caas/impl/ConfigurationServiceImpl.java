package com.juancavallotti.tools.caas.impl;

import com.juancavallotti.tools.caas.api.*;
import com.juancavallotti.tools.caas.spi.ConfigurationServiceBackend;
import com.juancavallotti.tools.caas.spi.ConfigurationServiceBackendException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.InputStream;
import java.util.Map;

public class ConfigurationServiceImpl implements Configuration {


    private static final Logger logger = LoggerFactory.getLogger(ConfigurationServiceImpl.class);

    @Autowired
    private ConfigurationServiceBackend backend;

    @Override
    public GetConfigurationResponse getConfiguration() {
        logger.debug("Trying to accquire all configs");
        try {
            return GetConfigurationResponse.respond200WithApplicationJson(backend.listConfigurations());
        } catch (ConfigurationServiceBackendException ex) {
            switch (ex.getCauseType()) {
                case OPERATION_NOT_SUPPORTED:
                    return GetConfigurationResponse.respond400WithApplicationJson(status("Not supported"));
                default:
                    return GetConfigurationResponse.respond500();
            }
        }

    }

    @Override
    public PostConfigurationResponse postConfiguration(DefaultConfigurationElement entity) {

        try {
            backend.createNewConfiguration(entity);
            return PostConfigurationResponse.respond202();
        } catch (ConfigurationServiceBackendException ex) {
            switch (ex.getCauseType()) {
                case VALIDATION:
                    return PostConfigurationResponse.respond400WithApplicationJson(status(ex.getMessage()));
                case OPERATION_NOT_SUPPORTED:
                    return PostConfigurationResponse.respond400WithApplicationJson(status("Not supported"));
                default:
                    return PostConfigurationResponse.respond500();
            }
        }
    }

    @Override
    public PostConfigurationCopyResponse postConfigurationCopy() {
        return null;
    }

    @Override
    public GetAppConfigurationResponse getApplicationConfiguration(String app, String env, String version) {
        try {
            return GetAppConfigurationResponse.respond200WithApplicationJson(backend.findConfiguration(app, env, version));
        } catch (ConfigurationServiceBackendException ex) {
            return GetAppConfigurationResponse.respond404WithTextPlain("Not Found");
        }
    }

    @Override
    public PutAppConfigurationResponse putConfiguration(Object entity) {
        return null;
    }

    @Override
    public PatchAppConfigurationResponse patchConfiguration(Object entity) {
        return null;
    }

    @Override
    public GetConfigurationDynamicResponse getConfigurationDynamic(String app, String version, String env, String key) {

        ConfigCoordinate coordinate = coordinate(app, version, env);

        try {
            DocumentData data = backend.getDocumentData(coordinate, key);
            return GetConfigurationDynamicResponse.respond200WithContentType(data.getData(), data.getDocument().getType());
        } catch (ConfigurationServiceBackendException e) {
            switch (e.getCauseType()) {
                case ENTITY_NOT_FOUND:
                    return GetConfigurationDynamicResponse.respond404WithTextPlain(e.getMessage());
                default:
                    return GetConfigurationDynamicResponse.respond500();
            }
        }
    }

    @Override
    public PutConfigurationDynamicResponse putConfigurationDynamic(String app, String version, String env, String key, String contentType, InputStream body) {

        logger.debug("Called method to add a document with key: {}, environment: {}, version: {}, key: {}", app, env, version, key);

        ConfigCoordinate coordinate = coordinate(app, version, env);

        try {
            backend.setDocument(coordinate, key, contentType, body);
            return PutConfigurationDynamicResponse.respond202();
        } catch (ConfigurationServiceBackendException ex) {
            switch (ex.getCauseType()) {
                case ENTITY_NOT_FOUND:
                    return PutConfigurationDynamicResponse.respond400WithApplicationJson(Map.of("status", ex.getMessage()));
                default:
                    return PutConfigurationDynamicResponse.respond500();
            }
        }
    }

    @Override
    public PostConfigurationPromoteResponse postConfigurationPromote() {
        return null;
    }


    private Map<String, String> status(String status) {
        return Map.of("status", status);
    }

    private ConfigCoordinate coordinate(String app, String version, String env) {
        DefaultConfigCoordinate coordinate = new DefaultConfigCoordinate();
        coordinate.setApplication(app);
        coordinate.setEnvironment(env);
        coordinate.setVersion(version);

        return coordinate;
    }
}
