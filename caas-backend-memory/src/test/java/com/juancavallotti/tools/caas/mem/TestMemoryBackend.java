package com.juancavallotti.tools.caas.mem;


import com.juancavallotti.tools.caas.api.ConfigCoordinate;
import com.juancavallotti.tools.caas.api.ConfigurationElement;
import com.juancavallotti.tools.caas.api.DefaultConfigurationElement;
import com.juancavallotti.tools.caas.spi.ConfigurationServiceBackendException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("In-memory backend tests...")
public class TestMemoryBackend {

    private MemoryConfigurationServiceBackend backend;

    @BeforeEach
    public void initTest() {
        backend = new MemoryConfigurationServiceBackend();
        backend.init();
    }

    @Test
    @DisplayName("Test add and Retrieve")
    public void testAddAndRetrieve() throws ConfigurationServiceBackendException {

        DefaultConfigurationElement elm = new DefaultConfigurationElement();
        elm.setApplication("myapp");
        elm.setEnvironment("dev");
        elm.setVersion("1.0");

        //create some properties.
        elm.setProperties(new DefaultConfigurationElement.DefaultPropertiesType());
        elm.getProperties().putAll(Map.of("test", "property"));

        //this should not fail.
        ConfigurationElement created = (ConfigurationElement) backend.createNewConfiguration(elm);

        assertEquals(elm, created, "Configurations should be equals.");

        //now try and retrieve
        ConfigurationElement existing = backend.findConfiguration(elm.getApplication(), elm.getVersion(), elm.getEnvironment());

        assertSame(created, existing, "Configurations should be the same instance.");
    }

}
