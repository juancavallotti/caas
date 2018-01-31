package com.juancavallotti.tools.caas.impl;

import com.juancavallotti.tools.caas.api.*;
import com.juancavallotti.tools.caas.spi.ConfigurationServiceBackend;
import com.juancavallotti.tools.caas.spi.ConfigurationServiceBackendException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.InputStream;
import java.util.List;
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
                    logger.error("Unknown backend exception", ex);
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
                case ENTITY_NOT_FOUND:
                    return PostConfigurationResponse.respond400WithApplicationJson(status(ex.getMessage()));
                case OPERATION_NOT_SUPPORTED:
                    return PostConfigurationResponse.respond400WithApplicationJson(status("Not supported"));
                default:
                    logger.error("Unknown backend exception", ex);
                    return PostConfigurationResponse.respond500();
            }
        }
    }

    @Override
    public PostConfigurationCopyResponse postConfigurationCopy(String app, String version, String targetVersion) {
        try {
            List<ConfigurationElement> newElements = backend.createNewVersion(app, version, targetVersion);
            logger.info("Copy operation finished. New configurations count: {}", newElements.size());
            return PostConfigurationCopyResponse.respond202();
        } catch (ConfigurationServiceBackendException ex) {
            switch (ex.getCauseType()) {
                case OPERATION_NOT_SUPPORTED:
                    return PostConfigurationCopyResponse.respond400WithApplicationJson(status("Not supported"));
                case VALIDATION:
                    return PostConfigurationCopyResponse.respond400WithApplicationJson(status(ex.getMessage()));
                case ENTITY_NOT_FOUND:
                    return PostConfigurationCopyResponse.respond404();
                default:
                    logger.error("Unknown backend exception", ex);
                    return PostConfigurationCopyResponse.respond500();
            }
        }
    }

    @Override
    public GetAppConfigurationResponse getApplicationConfiguration(String app, String version, String env) {
        try {
            return GetAppConfigurationResponse.respond200WithApplicationJson(backend.findConfiguration(app, version, env));
        } catch (ConfigurationServiceBackendException ex) {
            return GetAppConfigurationResponse.respond404WithTextPlain("Not Found");
        }
    }

    @Override
    public PutAppConfigurationResponse putConfiguration(String app, String version, String env, DefaultConfigurationElement entity) {

        logger.debug("Called PUT for app config with coordinates {} {} {}", app, version, env);

        //user may be sending some values in the json, we don't want them we want the path params.
        entity = coordinate(app, version, env, entity);

        try {
            backend.replaceConfiguration(entity);
            return PutAppConfigurationResponse.respond202();
        } catch (ConfigurationServiceBackendException ex) {

            switch (ex.getCauseType()) {
                case ENTITY_NOT_FOUND:
                case VALIDATION:
                    return PutAppConfigurationResponse.respond400WithApplicationJson(status(ex.getMessage()));
                case OPERATION_NOT_SUPPORTED:
                    return PutAppConfigurationResponse.respond400WithApplicationJson(status("Not supported"));
            }
            logger.error("Unknown backend exception", ex);
            return PutAppConfigurationResponse.respond500();
        }
    }

    @Override
    public PatchAppConfigurationResponse patchConfiguration(String app, String version, String env, DefaultConfigurationElement entity) {

        logger.debug("Called PATCH for app config with coordinates {} {} {}", app, version, env);

        //user may be sending some values in the json, we don't want them we want the path params.
        entity = coordinate(app, version, env, entity);

        try {
            backend.patchConfiguration(entity);
            return PatchAppConfigurationResponse.respond202();
        } catch (ConfigurationServiceBackendException ex) {

            switch (ex.getCauseType()) {
                case ENTITY_NOT_FOUND:
                case VALIDATION:
                    return PatchAppConfigurationResponse.respond400WithApplicationJson(status(ex.getMessage()));
                case OPERATION_NOT_SUPPORTED:
                    return PatchAppConfigurationResponse.respond400WithApplicationJson(status("Not supported"));
            }
            logger.error("Unknown backend exception", ex);
            return PatchAppConfigurationResponse.respond500();
        }
    }

    @Override
    public GetConfigurationDynamicResponse getConfigurationDynamic(String app, String version, String env, String key) {

        ConfigCoordinate coordinate = coordinate(app, version, env);

        try {
            DocumentData data = backend.getDocumentData(coordinate, key);
            return GetConfigurationDynamicResponse.respond200WithContentType(data.getData(), data.getDocument().getType());
        } catch (ConfigurationServiceBackendException ex) {
            switch (ex.getCauseType()) {
                case ENTITY_NOT_FOUND:
                    return GetConfigurationDynamicResponse.respond404WithTextPlain(ex.getMessage());
                default:
                    logger.error("Unknown backend exception", ex);
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
                    return PutConfigurationDynamicResponse.respond400WithApplicationJson(status(ex.getMessage()));
                default:
                    logger.error("Unknown backend exception", ex);
                    return PutConfigurationDynamicResponse.respond500();
            }
        }
    }

    @Override
    public PostConfigurationPromoteResponse postConfigurationPromote(String app, String version, String env, String targetEnv) {

        ConfigCoordinate coordinate = coordinate(app, version, env);

        try {
            backend.promoteConfiguration(coordinate, targetEnv);
            return PostConfigurationPromoteResponse.respond202();
        } catch (ConfigurationServiceBackendException ex) {
            switch (ex.getCauseType()) {
                case ENTITY_NOT_FOUND:
                case VALIDATION:
                    return PostConfigurationPromoteResponse.respond400WithApplicationJson(status(ex.getMessage()));
                case OPERATION_NOT_SUPPORTED:
                    return PostConfigurationPromoteResponse.respond400WithApplicationJson(status("Not supported"));
                default:
                    logger.error("Unknown backend exception", ex);
                    return PostConfigurationPromoteResponse.respond500();
            }
        }
    }


    private Map<String, String> status(String status) {
        return Map.of("status", status);
    }

    private ConfigCoordinate coordinate(String app, String version, String env) {
        return coordinate(app, version, env, new DefaultConfigCoordinate());
    }

    private <T extends ConfigCoordinate> T coordinate(String app, String version, String env, T target) {
        target.setApplication(app);
        target.setVersion(version);
        target.setEnvironment(env);
        return target;
    }
}
