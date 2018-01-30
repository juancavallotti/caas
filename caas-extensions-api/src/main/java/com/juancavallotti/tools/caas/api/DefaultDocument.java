package com.juancavallotti.tools.caas.api;

import java.lang.String;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DefaultDocument that = (DefaultDocument) o;
        return Objects.equals(getKey(), that.getKey()) &&
                Objects.equals(getType(), that.getType());
    }

    @Override
    public int hashCode() {

        return Objects.hash(getKey(), getType());
    }
}
