package com.juancavallotti.tools.caas.encryption;

import com.juancavallotti.tools.caas.api.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class EncryptionDataProcessorTest {

    @Test
    public void checkBaseFunctionality() throws IOException {

        String unencrypted = "hello secret world!";
        String key = "my.value";
        EncryptionProperties config = new EncryptionProperties();
        config.setAlgorithm("AES/CBC/PKCS5PADDING");
        config.setEncryptionKey("awesomepassword!awesome!");



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


    @Test
    public void checkWithKeyStore() throws Exception {
        String unencrypted = "hello secret world!";
        String key = "my.value";

        EncryptionProperties config = new EncryptionProperties();
        config.setAlgorithm("AES/CBC/PKCS5PADDING");
        config.setKeystoreLocation("testKeystore.jcks");
        config.setKeyAlias("enc-key");
        config.setKeystorePassword("changeit");
        config.setKeyPassword("changeit");

        
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
