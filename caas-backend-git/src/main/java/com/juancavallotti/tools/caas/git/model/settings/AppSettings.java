package com.juancavallotti.tools.caas.git.model.settings;

import com.juancavallotti.tools.caas.api.ConfigCoordinate;
import com.juancavallotti.tools.caas.api.DefaultConfigCoordinate;

import java.util.List;
import java.util.Map;

public class AppSettings {
    private String docsPrefix;
    private Map<String, EnvironmentSettings> environments;
    private List<DefaultConfigCoordinate> parents;

    public String getDocsPrefix() {
        return docsPrefix;
    }

    public void setDocsPrefix(String docsPrefix) {
        this.docsPrefix = docsPrefix;
    }

    public Map<String, EnvironmentSettings> getEnvironments() {
        return environments;
    }

    public void setEnvironments(Map<String, EnvironmentSettings> environments) {
        this.environments = environments;
    }

    public List<DefaultConfigCoordinate> getParents() {
        return parents;
    }

    public void setParents(List<DefaultConfigCoordinate> parents) {
        this.parents = parents;
    }

    public final static AppSettingsBuilder builder() {
        return new AppSettingsBuilder();
    }
}
