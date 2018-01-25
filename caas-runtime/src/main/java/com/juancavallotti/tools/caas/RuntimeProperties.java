package com.juancavallotti.tools.caas;

/**
 * List of constants applicable to the runtime.
 */
public interface RuntimeProperties {

    /**
     * The location where the configuration file is read. This must be provided as a System property.
     */
    String CONFIG_LOCATION = "config.location";

    /**
     * The implementation of the backend. This is optional and if not present, the first available will be picked.
     */
    String RUNTIME_BACKEND = "runtime.backend";
}
