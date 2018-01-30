package com.juancavallotti.tools.caas.api;

import java.lang.String;

/**
 * Default pojos should make module developer's life easier.
 */
public class DefaultDocument implements Document {
    private String key;

    private String type;

    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
