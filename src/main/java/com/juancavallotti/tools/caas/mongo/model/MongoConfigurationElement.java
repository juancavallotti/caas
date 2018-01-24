package com.juancavallotti.tools.caas.mongo.model;

import com.juancavallotti.tools.caas.api.ConfigCoordinate;
import com.juancavallotti.tools.caas.api.ConfigurationElement;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Document(collection = "configurations")
public class MongoConfigurationElement extends MongoConfigCoordinate implements ConfigurationElement {

    @Id
    private String id;

    private List<ConfigCoordinate> parents;

    private List<com.juancavallotti.tools.caas.api.Document> documents;

    private MongoConfigProperties properties;

    @Override
    public List<ConfigCoordinate> getParents() {
        return parents;
    }

    @Override
    public void setParents(List<ConfigCoordinate> parents) {
        this.parents = parents;
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
                    .getDocuments()
                    .stream()
                    .map(MongoDocument::fromDocument)
                    .collect(Collectors.toList()));
        }


        if (original.getParents() != null) {
            ret.setParents(original
                    .getParents()
                    .stream()
                    .map(MongoConfigCoordinate::fromCoordinate)
                    .collect(Collectors.toList()));
        }

        ret.setProperties(MongoConfigProperties.fromMap( (Map<String, String>)original.getProperties()));

        return ret;
    }
}
