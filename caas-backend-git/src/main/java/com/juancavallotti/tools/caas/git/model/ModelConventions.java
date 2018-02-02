package com.juancavallotti.tools.caas.git.model;

import java.util.Map;
import java.util.Set;

/**
 * Contains all conventional default configurations.
 */
public interface ModelConventions {

    Set<String> defaultGlobalAppNames = Set.of("global", "shared");

    /**
     * Default content type mapping.
     */
    Map<String, String> contentTypeMapping = Map.of(
            ".properties", "text/plain",
            ".json", "application/json",
            ".xml", "application/xml",
            ".dwl", "application/weave",
            ".txt", "text/plain",
            ".csv", "text/csv",
            ".yml", "application/x-yaml"
    );

    //Conventions that may be overriden by configuration.

    String defaultExtension = ".txt";

    String defaultContentType = contentTypeMapping.get(defaultExtension);

    String defaultDocsFolderPrefix = "docs_";

    String defaultPropertiesFileTemplate = "{application}_";

    //Constants are set in capitals.

    String GIT_DIR = ".git";

    String SETTINGS_FILE = "settings.yml";

    String ENVIRONMENT_PROPS_EXTENSION = ".properties";
}
