package com.juancavallotti.tools.caas.git.model.settings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
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

        //Constructor constructor = new Constructor(AppSettings.class);
        Yaml yaml = new Yaml();
        appSettings = yaml.loadAs(settingsStream, AppSettings.class);

        return appSettings;
    }



}
