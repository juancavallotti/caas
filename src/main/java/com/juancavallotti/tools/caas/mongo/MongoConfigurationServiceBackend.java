package com.juancavallotti.tools.caas.mongo;

import com.juancavallotti.tools.caas.api.ConfigCoordinate;
import com.juancavallotti.tools.caas.spi.ConfigurationServiceBackend;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class MongoConfigurationServiceBackend implements ConfigurationServiceBackend {

    private static final Logger logger = LoggerFactory.getLogger(MongoConfigurationServiceBackend.class);

    public MongoConfigurationServiceBackend() {
        logger.info(serviceName("Mongo"));
    }

    @Override
    public List<ConfigCoordinate> listConfigurations() {
        return null;
    }
}
