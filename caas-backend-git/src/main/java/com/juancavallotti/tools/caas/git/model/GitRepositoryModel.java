package com.juancavallotti.tools.caas.git.model;

import com.juancavallotti.tools.caas.api.ConfigCoordinate;
import com.juancavallotti.tools.caas.api.ConfigurationElement;
import com.juancavallotti.tools.caas.api.DefaultConfigurationElement;
import org.eclipse.jgit.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Class that models a git repository.
 */
public class GitRepositoryModel {

    private final List<GitConfigCoordinate> configs;

    private final List<GitConfigCoordinate> globalConfigs;

    public GitRepositoryModel(List<GitConfigCoordinate> configs, List<GitConfigCoordinate> globalConfigs) {
        this.configs = configs;
        this.globalConfigs = globalConfigs;
    }

    public List<ConfigCoordinate> listConfigs() {
        return Collections.unmodifiableList(configs);
    }

    public Optional<ConfigurationElement> findConfigurationElement(String app, String ver, String env) {

        Optional<GitConfigCoordinate> existing = findCoordinate(app, ver, env);

        if (!existing.isPresent()) {
            return Optional.empty();
        }

        //build the thing...
        GitConfigCoordinate gitConfigCoordinate = existing.get();
        ConfigurationElement element = gitConfigCoordinate.buildElement(globalConfigs, configs);
        return Optional.of(element);
    }

    public Optional<GitConfigCoordinate> findCoordinate(String app, String ver, String env) {
        return configs.stream()
                .filter(config -> StringUtils.equalsIgnoreCase(config.getApplication(), app)
                        && StringUtils.equalsIgnoreCase(config.getVersion(), ver)
                        && StringUtils.equalsIgnoreCase(config.getEnvironment(), env))
                .findAny();
    }

    public Optional<GitConfigCoordinate> findConfiguration(ConfigCoordinate coord) {
        return findCoordinate(coord.getApplication(), coord.getVersion(), coord.getEnvironment());
    }

}
