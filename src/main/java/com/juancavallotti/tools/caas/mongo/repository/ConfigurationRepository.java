package com.juancavallotti.tools.caas.mongo.repository;

import com.juancavallotti.tools.caas.mongo.model.MongoConfigurationElement;

import org.springframework.data.repository.CrudRepository;

public interface ConfigurationRepository extends CrudRepository<MongoConfigurationElement, String> {

    MongoConfigurationElement findByApplicationAndVersionAndEnvironment(String application, String version, String environment);

}
