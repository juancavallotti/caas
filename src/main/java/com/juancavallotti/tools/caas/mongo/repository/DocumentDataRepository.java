package com.juancavallotti.tools.caas.mongo.repository;

import com.juancavallotti.tools.caas.mongo.model.MongoDocumentData;
import org.springframework.data.repository.CrudRepository;

public interface DocumentDataRepository extends CrudRepository<MongoDocumentData, String> {

    public MongoDocumentData findByAppIdAndKey(String appId, String key);


}
