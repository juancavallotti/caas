package com.juancavallotti.tools.caas.spi;

import com.juancavallotti.tools.caas.api.ConfigCoordinate;
import com.juancavallotti.tools.caas.api.ConfigurationElement;
import com.juancavallotti.tools.caas.api.Document;
import com.juancavallotti.tools.caas.api.DocumentData;

import java.io.InputStream;
import java.util.List;

public interface ConfigurationServiceBackend {

    default String printServiceName() {
        return "CaaS Service Implementation: " + getServiceName();
    }

    default String getServiceName() {
        return getClass().getName();
    }

    List<ConfigCoordinate> listConfigurations();

    ConfigCoordinate createNewConfiguration(ConfigurationElement element) throws ConfigurationServiceBackendException;

    Document setDocument(ConfigCoordinate coordinate, String documentName, String contentType, InputStream documentData) throws ConfigurationServiceBackendException;

    DocumentData getDocumentData(ConfigCoordinate coordinate, String documentName) throws ConfigurationServiceBackendException;

    ConfigurationElement findConfiguration(String application, String environment, String version) throws ConfigurationServiceBackendException;
}
