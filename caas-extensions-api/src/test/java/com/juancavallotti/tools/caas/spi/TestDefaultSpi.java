package com.juancavallotti.tools.caas.spi;


import com.juancavallotti.tools.caas.api.ConfigCoordinate;
import com.juancavallotti.tools.caas.api.ConfigurationElement;
import com.juancavallotti.tools.caas.api.Document;
import com.juancavallotti.tools.caas.api.DocumentData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Test cases for default methods in SPI interface")
public class TestDefaultSpi {

    @Test
    public void checkCounts() {
        BasicImpl basicInstance = new BasicImpl();
        FullImpl fullInstance = new FullImpl();

        assertEquals(2, basicInstance.implementedFunctionality().size(), "Should have only the implemented count!");
        assertEquals(9, fullInstance.implementedFunctionality().size(), "Should have only the implemented count!");
    }


    public static class FullImpl implements ConfigurationServiceBackend {

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

    public static class BasicImpl implements ConfigurationServiceBackend {

        @Override
        public DocumentData getDocumentData(ConfigCoordinate coordinate, String documentName) throws ConfigurationServiceBackendException {
            return null;
        }

        @Override
        public ConfigurationElement findConfiguration(String application, String version, String env) throws ConfigurationServiceBackendException {
            return null;
        }
    }
}
