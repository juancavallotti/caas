package com.juancavallotti.tools.caas.encryption;

import com.juancavallotti.tools.caas.api.*;
import org.apache.logging.log4j.core.util.IOUtils;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

public class EncryptionDataProcessorTest {

    @Test
    public void checkBaseFunctionality() throws IOException {

        String unencrypted = "hello secret world!";
        String key = "my.value";
        EncryptionProperties config = new EncryptionProperties();
        config.setAlgoritm("AES/CBC/PKCS5PADDING");
        config.setEncryptionKey("awesomepassword!");



        EncryptionDataProcessor testSubject = new EncryptionDataProcessor(config);
        testSubject.init();

        //encrypt a value and then decrypt it.
        ConfigurationElement element = new DefaultConfigurationElement();
        ConfigurationElement.PropertiesType properties = new DefaultConfigurationElement.DefaultPropertiesType();
        properties.put(key, unencrypted);
        element.setProperties(properties);


        //test
        element = testSubject.processWriteConfig("testOperation", element);

        //check
        assertNotEquals(element.getProperties().get(key), unencrypted);


        //upon second initialization, it should be able to decrypt the value, as it is the typical use case.
        testSubject.init();

        //go reverse
        element = testSubject.processReadConfig("testOperation", element);

        assertEquals(element.getProperties().get(key), unencrypted);

    }


}
