package com.juancavallotti.tools.caas.mongo;

import com.juancavallotti.tools.caas.api.ConfigCoordinate;
import com.juancavallotti.tools.caas.api.ConfigurationElement;
import com.juancavallotti.tools.caas.mongo.model.MongoConfigurationElement;
import com.juancavallotti.tools.caas.mongo.repository.ConfigurationRepository;
import com.juancavallotti.tools.caas.spi.ConfigurationServiceBackend;
import com.juancavallotti.tools.caas.spi.ConfigurationServiceBackendException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.LinkedList;
import java.util.List;

public class MongoConfigurationServiceBackend implements ConfigurationServiceBackend {

    private static final Logger logger = LoggerFactory.getLogger(MongoConfigurationServiceBackend.class);

    @Autowired
    private ConfigurationRepository repository;

    public MongoConfigurationServiceBackend() {
        logger.info(serviceName("Mongo"));
    }

    @Override
    public List<ConfigCoordinate> listConfigurations() {

        logger.debug("Repository class is: {}", repository.getClass().getName());

        Iterable<MongoConfigurationElement> coordinates = repository.findAll();

        List<ConfigCoordinate> ret = new LinkedList<>();
        coordinates.forEach((c) -> {
            ret.add(standardizeProps(c));
        });

        return ret;
    }

    @Override
    public ConfigCoordinate createNewConfiguration(ConfigurationElement element) throws ConfigurationServiceBackendException {

        if (element.getDocuments() != null && !element.getDocuments().isEmpty()) {
            throw ConfigurationServiceBackendException.builder()
                    .setMessage("Configuration cannot have parents")
                    .setCauseType(ConfigurationServiceBackendException.ExceptionCause.VALIDATION)
                    .build();
        }

        repository.save(MongoConfigurationElement.fromConfigurationElement(element));
        return element;
    }

    @Override
    public ConfigurationElement findConfiguration(String application, String environment, String version) throws ConfigurationServiceBackendException {

        MongoConfigurationElement ret = repository.findByApplicationAndVersionAndEnvironment(application, environment, version);

        if (ret == null) {
            throw ConfigurationServiceBackendException.builder()
                    .setMessage("Config not found")
                    .setCauseType(ConfigurationServiceBackendException.ExceptionCause.ENTITY_NOT_FOUND)
                    .build();
        }

        return standardizeProps(ret);
    }

    private ConfigurationElement standardizeProps(MongoConfigurationElement elm) {
        elm.setProperties(elm.getProperties().standardize());
        return elm;
    }
}
