package com.juancavallotti.tools.caas.mongo.repository;

import com.juancavallotti.tools.caas.api.ConfigCoordinate;
import com.juancavallotti.tools.caas.mongo.model.MongoConfigCoordinate;
import com.juancavallotti.tools.caas.mongo.model.MongoConfigurationElement;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ConfigurationRepository extends CrudRepository<MongoConfigurationElement, String> {

    MongoConfigurationElement findByApplicationIgnoreCaseAndVersionAndEnvironmentIgnoreCase(String application, String version, String environment);

    @Query(value = "{}", fields = "{application: 1, version: 2, environment: 3}")
    List<MongoConfigCoordinate> findAllCoordinates();

}
