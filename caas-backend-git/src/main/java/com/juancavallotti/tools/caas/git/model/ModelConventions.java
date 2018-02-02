package com.juancavallotti.tools.caas.git.model;

import java.util.Map;
import java.util.Set;

/**
 * Contains all conventional default configurations.
 */
public interface ModelConventions {

    Set<String> defaultGlobalAppNames = Set.of("global", "shared");

    Map<String, String> contentTypeMapping = Map.of(
            ".properties", "text/plain",
            ".json", "application/json",
            ".xml", "application/xml",
            ".dwl", "application/weave",
            ".txt", "text/plain",
            ".csv", "text/plain"
    );

    String defaultExtension = ".txt";

    String defaultContentType = contentTypeMapping.get(defaultExtension);

    String defaultDocsFolderPrefix = "docs_";

    String GIT_DIR = ".git";


}
