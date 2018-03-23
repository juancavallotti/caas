package com.juancavallotti.tools.caas.mongo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.juancavallotti.tools.caas.api.ConfigCoordinate;
import com.juancavallotti.tools.caas.api.ConfigurationElement;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "configurations")
public class MongoConfigurationElement extends MongoConfigCoordinate implements ConfigurationElement {

    @Id
    @JsonIgnore //not the best but we embrace jackson... for now
    private String id;

    private List<ConfigCoordinate> imports;

    private List<com.juancavallotti.tools.caas.api.Document> documents;

    private MongoConfigProperties properties;

    @Override
    public List<ConfigCoordinate> getImports() {
        return imports;
    }

    @Override
    public void setImports(List<ConfigCoordinate> imports) {
        this.imports = imports;
    }

    @Override
    public MongoConfigProperties getProperties() {
        return properties;
    }

    @Override
    public void setProperties(PropertiesType properties) {
        this.properties = (MongoConfigProperties) properties;
    }

    public void setProperties(MongoConfigProperties properties) {
        this.properties = properties;
    }
    @Override
    public List<com.juancavallotti.tools.caas.api.Document> getDocuments() {
        return documents;
    }

    @Override
    public void setDocuments(List<com.juancavallotti.tools.caas.api.Document> documents) {
        this.documents = documents;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }




    public static MongoConfigurationElement fromConfigurationElement(ConfigurationElement original) {
        MongoConfigurationElement ret = new MongoConfigurationElement();

        fromConfigCoordinate(ret, original);

        if (original.getDocuments() != null) {
            ret.setDocuments(original
                    .getDocuments());
        }


        if (original.getImports() != null) {
            ret.setImports(toMongoCoordinates(original.getImports()));
        }

        ret.setProperties(MongoConfigProperties.fromMap(original.getProperties()));

        return ret;
    }
}
