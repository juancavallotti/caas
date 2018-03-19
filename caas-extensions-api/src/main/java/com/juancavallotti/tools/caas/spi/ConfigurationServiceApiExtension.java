package com.juancavallotti.tools.caas.spi;

/**
 * Extension point to add additional API endpoints to the configuration service. These service providers will be just
 * identified by this interface and configured through standard JAX-RS Annotations. All API extensions will be enabled
 * and registered in the context. If the extension is optionally configured and disabled, then it must throw the appropriate
 * {@link ConfigurationServiceBackendException}.
 */
public interface ConfigurationServiceApiExtension {
}
