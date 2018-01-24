package com.juancavallotti.tools.caas.impl;

import com.juancavallotti.tools.caas.api.Configuration;
import com.juancavallotti.tools.caas.api.ConfigurationElement;
import com.juancavallotti.tools.caas.api.DefaultConfigurationElement;
import com.juancavallotti.tools.caas.git.GitConfigurationServiceBackend;
import com.juancavallotti.tools.caas.spi.ConfigurationServiceBackend;
import com.juancavallotti.tools.caas.spi.ConfigurationServiceBackendException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.inject.Inject;
import java.util.Collections;
import java.util.Map;

public class ConfigurationServiceImpl implements Configuration {


    private static final Logger logger = LoggerFactory.getLogger(ConfigurationServiceImpl.class);

    @Autowired
    private ConfigurationServiceBackend backend;

    @Override
    public GetConfigurationResponse getConfiguration() {
        logger.debug("Trying to accquire all configs");
        return GetConfigurationResponse.respond200WithApplicationJson(backend.listConfigurations());
    }

    @Override
    public PostConfigurationResponse postConfiguration(DefaultConfigurationElement entity) {

        try {
            backend.createNewConfiguration(entity);
            return PostConfigurationResponse.respond202();
        } catch (ConfigurationServiceBackendException ex) {
            switch (ex.getCauseType()) {
                case VALIDATION:
                    return PostConfigurationResponse.respond400WithApplicationJson(Map.of("status", ex.getMessage()));
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
    public GetAppConfigurationResponse getApplicationConfiguration() {
        return null;
    }

    @Override
    public PostAppConfigurationResponse postConfiguration(Object entity) {
        return null;
    }

    @Override
    public PutConfigurationResponse putConfiguration(Object entity) {
        return null;
    }

    @Override
    public GetConfigurationDynamicResponse getConfigurationDynamic() {
        return null;
    }

    @Override
    public PutConfigurationDynamicResponse putConfigurationDynamic(String contentType, Object body) {
        return null;
    }

    @Override
    public PostConfigurationPromoteResponse postConfigurationPromote() {
        return null;
    }
}
