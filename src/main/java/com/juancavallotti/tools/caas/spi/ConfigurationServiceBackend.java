package com.juancavallotti.tools.caas.spi;

import com.juancavallotti.tools.caas.api.ConfigCoordinate;
import com.juancavallotti.tools.caas.api.ConfigurationElement;

import java.util.List;

public interface ConfigurationServiceBackend {

    default String serviceName(String name) {
        return "CaaS Service Implementation: " + name;
    }

    List<ConfigCoordinate> listConfigurations();

    public ConfigCoordinate createNewConfiguration(ConfigurationElement element) throws ConfigurationServiceBackendException;

    ConfigurationElement findConfiguration(String application, String environment, String version) throws ConfigurationServiceBackendException;
}
