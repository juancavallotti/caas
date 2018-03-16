package com.juancavallotti.tools.caas.impl;

import com.juancavallotti.tools.caas.api.*;
import com.juancavallotti.tools.caas.spi.ConfigurationServiceBackend;
import com.juancavallotti.tools.caas.spi.ConfigurationServiceBackendException;
import com.juancavallotti.tools.caas.spi.ConfigurationServiceDataProcessor;
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

    @Autowired
    private List<ConfigurationServiceDataProcessor> processors;

    @Override
    public ConfigurationServiceResponse getConfiguration() {
        logger.debug("Trying to accquire all configs");
        try {
            return ConfigurationServiceResponse.respond200WithApplicationJson(backend.listConfigurations());
        } catch (ConfigurationServiceBackendException ex) {
            switch (ex.getCauseType()) {
                case OPERATION_NOT_SUPPORTED:
                    return ConfigurationServiceResponse.respondOperationNotSupported();
                default:
                    logger.error("Unknown backend exception", ex);
                    return ConfigurationServiceResponse.respond500();
            }
        }

    }

    @Override
    public ConfigurationServiceResponse postConfiguration(DefaultConfigurationElement entity) {

        logger.debug("Trying to post a new configuration...");
        try {
            ConfigurationElement processedEntity = preProcessWriteEntity("postConfiguration" ,entity);
            backend.createNewConfiguration(processedEntity);
            return ConfigurationServiceResponse.respond202();
        } catch (ConfigurationServiceBackendException ex) {
            switch (ex.getCauseType()) {
                case VALIDATION:
                    return ConfigurationServiceResponse.respond400WithApplicationJson(status(ex.getMessage()));
                case ENTITY_NOT_FOUND:
                    return ConfigurationServiceResponse.respond400WithApplicationJson(status(ex.getMessage()));
                case OPERATION_NOT_SUPPORTED:
                    return ConfigurationServiceResponse.respondOperationNotSupported();
                default:
                    logger.error("Unknown backend exception", ex);
                    return ConfigurationServiceResponse.respond500();
            }
        }
    }

    @Override
    public ConfigurationServiceResponse postConfigurationCopy(String app, String version, String targetVersion) {
        try {
            List<ConfigurationElement> newElements = backend.createNewVersion(app, version, targetVersion);
            logger.info("Copy operation finished. New configurations count: {}", newElements.size());
            return ConfigurationServiceResponse.respond202();
        } catch (ConfigurationServiceBackendException ex) {
            switch (ex.getCauseType()) {
                case OPERATION_NOT_SUPPORTED:
                    return ConfigurationServiceResponse.respondOperationNotSupported();
                case VALIDATION:
                    return ConfigurationServiceResponse.respond400WithApplicationJson(status(ex.getMessage()));
                case ENTITY_NOT_FOUND:
                    return ConfigurationServiceResponse.respond404();
                default:
                    logger.error("Unknown backend exception", ex);
                    return ConfigurationServiceResponse.respond500();
            }
        }
    }

    @Override
    public ConfigurationServiceResponse getApplicationConfiguration(String app, String version, String env) {
        logger.debug("Called GET for app config with coordinates {} {} {}", app, version, env);
        try {
            ConfigurationElement elm = backend.findConfiguration(app, version, env);
            elm = postProcessReadEntity("getApplicationConfiguration", elm);

            return ConfigurationServiceResponse.respond200WithApplicationJson(elm);
        } catch (ConfigurationServiceBackendException ex) {
            return ConfigurationServiceResponse.respond404WithTextPlain("Not Found");
        }
    }

    @Override
    public ConfigurationServiceResponse putConfiguration(String app, String version, String env, DefaultConfigurationElement entity) {

        logger.debug("Called PUT for app config with coordinates {} {} {}", app, version, env);

        //user may be sending some values in the json, we don't want them we want the path params.
        entity = coordinate(app, version, env, entity);

        ConfigurationElement preProcessed = preProcessWriteEntity("putConfiguration", entity);

        try {
            backend.replaceConfiguration(preProcessed);
            return ConfigurationServiceResponse.respond202();
        } catch (ConfigurationServiceBackendException ex) {

            switch (ex.getCauseType()) {
                case ENTITY_NOT_FOUND:
                case VALIDATION:
                    return ConfigurationServiceResponse.respond400WithApplicationJson(status(ex.getMessage()));
                case OPERATION_NOT_SUPPORTED:
                    return ConfigurationServiceResponse.respondOperationNotSupported();
            }
            logger.error("Unknown backend exception", ex);
            return ConfigurationServiceResponse.respond500();
        }
    }

    @Override
    public ConfigurationServiceResponse patchConfiguration(String app, String version, String env, DefaultConfigurationElement entity) {

        logger.debug("Called PATCH for app config with coordinates {} {} {}", app, version, env);

        //user may be sending some values in the json, we don't want them we want the path params.
        entity = coordinate(app, version, env, entity);

        ConfigurationElement preProcessed = preProcessWriteEntity("patchConfiguration", entity);

        try {
            backend.patchConfiguration(preProcessed);
            return ConfigurationServiceResponse.respond202();
        } catch (ConfigurationServiceBackendException ex) {

            switch (ex.getCauseType()) {
                case ENTITY_NOT_FOUND:
                case VALIDATION:
                    return ConfigurationServiceResponse.respond400WithApplicationJson(status(ex.getMessage()));
                case OPERATION_NOT_SUPPORTED:
                    return ConfigurationServiceResponse.respondOperationNotSupported();
            }
            logger.error("Unknown backend exception", ex);
            return ConfigurationServiceResponse.respond500();
        }
    }

    @Override
    public ConfigurationServiceResponse getConfigurationDynamic(String app, String version, String env, String key) {

        logger.debug("Called GET for app document with coordinates {} {} {} and key", app, version, env, key);

        ConfigCoordinate coordinate = coordinate(app, version, env);

        try {
            DocumentData data = backend.getDocumentData(coordinate, key);

            data = postProcessReadDocument("getConfigurationDynamic", data);

            return ConfigurationServiceResponse.respond200WithContentType(data.getData(), data.getDocument().getType());
        } catch (ConfigurationServiceBackendException ex) {
            switch (ex.getCauseType()) {
                case ENTITY_NOT_FOUND:
                    return ConfigurationServiceResponse.respond404WithTextPlain(ex.getMessage());
                default:
                    logger.error("Unknown backend exception", ex);
                    return ConfigurationServiceResponse.respond500();
            }
        }
    }

    @Override
    public ConfigurationServiceResponse putConfigurationDynamic(String app, String version, String env, String key, String contentType, InputStream body) {

        logger.debug("Called method to add a document with key: {}, forEnvironment: {}, version: {}, key: {}", app, env, version, key);

        ConfigCoordinate coordinate = coordinate(app, version, env);

        DocumentData documentData = new DefaultDocumentData(new DefaultDocument(key, contentType), body);

        documentData = preProcessSaveDocument("putConfigurationDynamic", documentData);

        try {
            //TODO - Improve the interface to take DocumentData instead of separate params.
            backend.setDocument(coordinate, documentData.getDocument().getKey()
                    , documentData.getDocument().getType()
                    , documentData.getData());
            return ConfigurationServiceResponse.respond202();
        } catch (ConfigurationServiceBackendException ex) {
            switch (ex.getCauseType()) {
                case ENTITY_NOT_FOUND:
                    return ConfigurationServiceResponse.respond400WithApplicationJson(status(ex.getMessage()));
                case OPERATION_NOT_SUPPORTED:
                    return ConfigurationServiceResponse.respondOperationNotSupported();
                default:
                    logger.error("Unknown backend exception", ex);
                    return ConfigurationServiceResponse.respond500();
            }
        }
    }

    @Override
    public ConfigurationServiceResponse postConfigurationPromote(String app, String version, String env, String targetEnv) {

        logger.debug("Called POST configuration promote with coordinates {} {} {} and targetEnv", app, version, env, targetEnv);

        ConfigCoordinate coordinate = coordinate(app, version, env);

        try {
            backend.promoteConfiguration(coordinate, targetEnv);
            return ConfigurationServiceResponse.respond202();
        } catch (ConfigurationServiceBackendException ex) {
            switch (ex.getCauseType()) {
                case ENTITY_NOT_FOUND:
                case VALIDATION:
                    return ConfigurationServiceResponse.respond400WithApplicationJson(status(ex.getMessage()));
                case OPERATION_NOT_SUPPORTED:
                    return ConfigurationServiceResponse.respondOperationNotSupported();
                default:
                    logger.error("Unknown backend exception", ex);
                    return ConfigurationServiceResponse.respond500();
            }
        }
    }


    private ConfigurationElement preProcessWriteEntity(String opName, ConfigurationElement configurationElement) {
        ConfigurationElement ret = configurationElement;
        logger.debug("Pre processing write, operation: {}", opName);

        for(ConfigurationServiceDataProcessor processor : processors) {
            logger.debug("Calling processor: {}", processor.getClass().getName());
            ret = processor.processWriteConfig(opName, ret);
        }

        return ret;
    }

    private ConfigurationElement postProcessReadEntity(String opName, ConfigurationElement configurationElement) {
        ConfigurationElement ret = configurationElement;

        logger.debug("Post processing read, operation: {}", opName);

        for(ConfigurationServiceDataProcessor processor : processors) {
            logger.debug("Calling processor: {}", processor.getClass().getName());
            ret = processor.processReadConfig(opName, ret);
        }

        return ret;
    }


    private DocumentData preProcessSaveDocument(String opName, DocumentData document) {

        DocumentData ret = document;
        logger.debug("Pre processing save document, operation: {}", opName);

        for(ConfigurationServiceDataProcessor processor : processors) {
            logger.debug("Calling processor: {}", processor.getClass().getName());
            ret = processor.processWriteDocument(opName, ret);
        }

        return ret;
    }

    private DocumentData postProcessReadDocument(String opName, DocumentData document) {

        DocumentData ret = document;
        logger.debug("Post processing read document, operation: {}", opName);

        for(ConfigurationServiceDataProcessor processor : processors) {
            logger.debug("Calling processor: {}", processor.getClass().getName());
            ret = processor.processReadDocument(opName, ret);
        }

        return ret;
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
