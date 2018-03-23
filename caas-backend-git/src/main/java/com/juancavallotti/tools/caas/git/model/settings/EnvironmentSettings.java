package com.juancavallotti.tools.caas.git.model.settings;

import com.juancavallotti.tools.caas.api.DefaultConfigCoordinate;

import java.util.List;

public class EnvironmentSettings {

    private String documentsPath;

    private List<DefaultConfigCoordinate> imports;

    public String getDocumentsPath() {
        return documentsPath;
    }

    public void setDocumentsPath(String documentsPath) {
        this.documentsPath = documentsPath;
    }

    public List<DefaultConfigCoordinate> getImports() {
        return imports;
    }

    public void setImports(List<DefaultConfigCoordinate> imports) {
        this.imports = imports;
    }
}
