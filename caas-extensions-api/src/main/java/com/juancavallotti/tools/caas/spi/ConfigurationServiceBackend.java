package com.juancavallotti.tools.caas.spi;

import com.juancavallotti.tools.caas.api.*;

import java.io.InputStream;
import java.util.List;

public interface ConfigurationServiceBackend {

    default String printServiceName() {
        return "CaaS Service Implementation: " + getServiceName();
    }

    default String getServiceName() {
        return getClass().getName();
    }

    /**
     * Return a list of all existing configurations. This is an optional operation.
     * @return
     */
    default List<ConfigCoordinate> listConfigurations() throws ConfigurationServiceBackendException {
        throw ConfigurationServiceBackendException.notSupported();
    }

    /**
     * Create a new configuration in the backend. This is an optional operation.
     * @param element
     * @return
     * @throws ConfigurationServiceBackendException
     */
    default ConfigCoordinate createNewConfiguration(ConfigurationElement element) throws ConfigurationServiceBackendException {
        throw ConfigurationServiceBackendException.notSupported();
    }

    /**
     * Create a new document in the backend for the particular coordinate. This is an optional operation.
     * @param coordinate
     * @param documentName
     * @param contentType
     * @param documentData
     * @return
     * @throws ConfigurationServiceBackendException
     */
    default Document setDocument(ConfigCoordinate coordinate, String documentName, String contentType, InputStream documentData) throws ConfigurationServiceBackendException {
        throw ConfigurationServiceBackendException.notSupported();
    }

    /**
     * Read the information of a particular document in the service.
     * This is a mandatory operation. All backends must support reading documents.
     * @param coordinate
     * @param documentName
     * @return
     * @throws ConfigurationServiceBackendException
     */
    DocumentData getDocumentData(ConfigCoordinate coordinate, String documentName) throws ConfigurationServiceBackendException;

    /**
     * Read a configuration in the configuration service.
     * This is a mandatory operation. All backends must support reading configurations.
     * What would be the purpose of the service otherwise?
     * @param application
     * @param environment
     * @param version
     * @return
     * @throws ConfigurationServiceBackendException
     */
    ConfigurationElement findConfiguration(String application, String version, String env) throws ConfigurationServiceBackendException;

    /**
     * Update completely an existing configuration.
     * NOTE: this method must not remove any of the existing documents. If user provides documents
     * there is the option to ignore them or to throw invalid data exception.
     *
     * This is an optional operation.
     * @param entity the configuration to update.
     * @return The updated configuration.
     * @throws ConfigurationServiceBackendException
     */
    ConfigurationElement replaceConfiguration(ConfigurationElement entity) throws ConfigurationServiceBackendException;
}
