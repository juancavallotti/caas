package com.juancavallotti.tools.caas.mem;

import com.juancavallotti.tools.caas.api.ConfigCoordinate;
import com.juancavallotti.tools.caas.api.ConfigurationElement;
import com.juancavallotti.tools.caas.api.Document;
import com.juancavallotti.tools.caas.api.DocumentData;
import com.juancavallotti.tools.caas.spi.ConfigurationServiceBackend;
import com.juancavallotti.tools.caas.spi.ConfigurationServiceBackendException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.io.InputStream;
import java.util.List;

public class MemoryConfigurationServiceBackend implements ConfigurationServiceBackend {

    private static final Logger logger = LoggerFactory.getLogger(MemoryConfigurationServiceBackend.class);

    @PostConstruct
    public void init() {
        logger.warn("Initializing In-Memory backend, NOTE: use it only for demo purposes!!!");
    }

    @Override
    public List<ConfigCoordinate> listConfigurations() throws ConfigurationServiceBackendException {
        return null;
    }

    @Override
    public ConfigCoordinate createNewConfiguration(ConfigurationElement element) throws ConfigurationServiceBackendException {
        return null;
    }

    @Override
    public Document setDocument(ConfigCoordinate coordinate, String documentName, String contentType, InputStream documentData) throws ConfigurationServiceBackendException {
        return null;
    }

    @Override
    public DocumentData getDocumentData(ConfigCoordinate coordinate, String documentName) throws ConfigurationServiceBackendException {
        return null;
    }

    @Override
    public ConfigurationElement findConfiguration(String application, String version, String env) throws ConfigurationServiceBackendException {
        return null;
    }

    @Override
    public ConfigurationElement replaceConfiguration(ConfigurationElement entity) throws ConfigurationServiceBackendException {
        return null;
    }

    @Override
    public ConfigurationElement patchConfiguration(ConfigurationElement entity) throws ConfigurationServiceBackendException {
        return null;
    }

    @Override
    public ConfigurationElement promoteConfiguration(ConfigCoordinate coordinate, String targetEnvironment) throws ConfigurationServiceBackendException {
        return null;
    }

    @Override
    public <T extends ConfigurationElement> List<T> createNewVersion(String appName, String version, String targetVersion) throws ConfigurationServiceBackendException {
        return null;
    }
}
