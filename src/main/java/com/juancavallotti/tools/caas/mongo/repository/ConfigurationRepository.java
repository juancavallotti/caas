package com.juancavallotti.tools.caas.mongo.repository;

import com.juancavallotti.tools.caas.mongo.model.MongoConfigCoordinate;

import org.springframework.data.repository.Repository;

public interface ConfigurationRepository extends Repository<MongoConfigCoordinate, String> {
}
