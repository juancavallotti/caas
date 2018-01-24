package com.juancavallotti.tools.caas.spi;

import com.juancavallotti.tools.caas.api.ConfigCoordinate;

import java.util.List;

public interface ConfigurationServiceBackend {

    default String serviceName(String name) {
        return "CaaS Service Implementation: " + name;
    }

    List<ConfigCoordinate> listConfigurations();


}
