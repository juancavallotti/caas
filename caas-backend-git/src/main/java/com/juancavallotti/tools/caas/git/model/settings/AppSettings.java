package com.juancavallotti.tools.caas.git.model.settings;

import com.juancavallotti.tools.caas.api.ConfigCoordinate;
import com.juancavallotti.tools.caas.api.DefaultConfigCoordinate;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

    /**
     * Tries to locate the settings in the environments collection, otherwise returns a default forEnvironment.
     * @param name the name of the forEnvironment whose settings need to be looked up.
     * @return
     */
    public EnvironmentSettings forEnvironment(String name) {
        return Optional.ofNullable(environments.get(name)).orElseGet(() -> defaultEnvironment(name));
    }

    public EnvironmentSettings defaultEnvironment(String name) {

        EnvironmentSettings ret = new EnvironmentSettings();

        ret.setParents(Collections.emptyList());
        ret.setDocumentsPath(getDocsPrefix() + name);

        return ret;
    }


    public String buildPropertiesFileTemplate(String appName, String version) {

        String ret = propertiesFileTemplate.replaceAll("\\{application\\}", appName);
        ret = ret.replaceAll("\\{version\\}", version);

        return ret;
    }

}
