package com.juancavallotti.tools.caas.mongo.model;

import com.juancavallotti.tools.caas.api.Document;

public class MongoDocument implements Document{

    private String key;
    private String type;


    @Override
    public String getKey() {
        return key;
    }

    @Override
    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public void setType(String type) {
        this.type = type;
    }

    public static MongoDocument fromDocument(Document doc) {
        MongoDocument ret = new MongoDocument();

        ret.setKey(doc.getKey());
        ret.setType(doc.getType());

        return ret;
    }


}
