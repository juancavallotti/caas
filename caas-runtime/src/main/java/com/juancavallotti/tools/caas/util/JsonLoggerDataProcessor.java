package com.juancavallotti.tools.caas.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.juancavallotti.tools.caas.api.ConfigurationElement;
import com.juancavallotti.tools.caas.spi.ConfigurationServiceDataProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonLoggerDataProcessor implements ConfigurationServiceDataProcessor {

    private static final Logger logger = LoggerFactory.getLogger(JsonLoggerDataProcessor.class);

    private ObjectMapper om = new ObjectMapper();

    @Override
    public boolean enabledByDefault() {
        return false;
    }

    @Override
    public ConfigurationElement processReadConfig(String operationName, ConfigurationElement original) {

        logConfigurationElement(operationName, original);

        return original;
    }

    @Override
    public ConfigurationElement processWriteConfig(String operationName, ConfigurationElement original) {

        logConfigurationElement(operationName, original);

        return original;
    }


    private void logConfigurationElement(String operationName, ConfigurationElement original) {

        if (!logger.isInfoEnabled()) {
            return;
        }

        try {

            logger.info("Called operation {} with data {}", operationName, om.writeValueAsString(original));

        } catch (JsonProcessingException ex) {
            logger.error("Error while converting data to json: ", ex);
        }
    }
}
