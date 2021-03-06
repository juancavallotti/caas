package com.juancavallotti.tools.caas.git.model.settings;

import com.juancavallotti.tools.caas.git.model.ModelConventions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.representer.Representer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Objects;

public class AppSettingsBuilder {

    private static final Logger logger = LoggerFactory.getLogger(AppSettingsBuilder.class);

    private InputStream settingsStream;

    /**
     * Read settings from a file.
     * @param settingsPath
     * @return
     */
    public AppSettingsBuilder fromFile(File settingsPath) {
        return fromInputStream(Objects.requireNonNull(settingsStream));
    }

    /**
     * Read settings from path.
     * @param settingsPath
     * @return
     */
    public AppSettingsBuilder fromPath(Path settingsPath) {
        try {
            return fromInputStream(Files.newInputStream(Objects.requireNonNull(settingsPath)));
        } catch (IOException ex) {
            throw new RuntimeException("Cannot read configuration file:", ex);
        }
    }

    public AppSettingsBuilder fromInputStream(InputStream is) {
        this.settingsStream = Objects.requireNonNull(is);
        return this;
    }

    public AppSettings build() {

        AppSettings appSettings = null;

        Representer rep = new Representer();
        rep.getPropertyUtils().setSkipMissingProperties(true);
        Constructor constructor = new Constructor(AppSettings.class);
        Yaml yaml = new Yaml(constructor, rep);
        appSettings = yaml.loadAs(settingsStream, AppSettings.class);

        if (appSettings == null) {
            //no configuration
            appSettings = new AppSettings();
        }

        fillDefaults(appSettings);

        if (logger.isDebugEnabled()) {
            logger.debug("Effective settings: \n{}", dumpConfig(appSettings));
        }

        return appSettings;
    }

    public AppSettings defaults() {

        AppSettings appSettings = new AppSettings();

        fillDefaults(appSettings);

        return appSettings;
    }

    private void fillDefaults(AppSettings settings) {
        if (settings.getDocsPrefix() == null) {
            settings.setDocsPrefix(ModelConventions.defaultDocsFolderPrefix);
        }

        if (settings.getPropertiesFileTemplate() == null) {
            settings.setPropertiesFileTemplate(ModelConventions.defaultPropertiesFileTemplate);
        }

        if (settings.getImports() == null) {
            settings.setImports(Collections.emptyList());
        }

        if (settings.getEnvironments() == null) {
            settings.setEnvironments(Collections.emptyMap());
        }
    }

    public static String dumpConfig(AppSettings config) {
        return new Yaml().dump(config);
    }

}
