package com.juancavallotti.tools.caas.git.model.settings;

import com.juancavallotti.tools.caas.api.ConfigCoordinate;
import com.juancavallotti.tools.caas.api.DefaultConfigCoordinate;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

public class AppSettings {
    private String docsPrefix;
    private Map<String, EnvironmentSettings> environments;
    private List<DefaultConfigCoordinate> parents;
    private String propertiesFileTemplate;

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

    public String getPropertiesFileTemplate() {
        return propertiesFileTemplate;
    }

    public void setPropertiesFileTemplate(String propertiesFileTemplate) {
        this.propertiesFileTemplate = propertiesFileTemplate;
    }

    public final static AppSettingsBuilder builder() {
        return new AppSettingsBuilder();
    }

    public String buildPropertiesFileTemplate(String appName, String version) {

        String ret = propertiesFileTemplate.replaceAll("\\{application\\}", appName);
        ret = ret.replaceAll("\\{version\\}", version);

        return ret;
    }

}
