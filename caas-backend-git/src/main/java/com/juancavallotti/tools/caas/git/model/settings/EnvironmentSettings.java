package com.juancavallotti.tools.caas.git.model.settings;

import com.juancavallotti.tools.caas.api.DefaultConfigCoordinate;

import java.util.List;
import java.util.Objects;

public class EnvironmentSettings {

    private String documentsPath;

    private List<DefaultConfigCoordinate> parents;

    public String getDocumentsPath() {
        return documentsPath;
    }

    public void setDocumentsPath(String documentsPath) {
        this.documentsPath = documentsPath;
    }

    public List<DefaultConfigCoordinate> getParents() {
        return parents;
    }

    public void setParents(List<DefaultConfigCoordinate> parents) {
        this.parents = parents;
    }
}
