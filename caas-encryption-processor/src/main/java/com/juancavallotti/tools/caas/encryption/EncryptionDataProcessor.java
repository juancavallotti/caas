package com.juancavallotti.tools.caas.encryption;

import com.juancavallotti.tools.caas.api.*;
import com.juancavallotti.tools.caas.spi.ConfigurationServiceDataProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.crypto.*;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.Map;
import java.util.function.Function;

public class EncryptionDataProcessor implements ConfigurationServiceDataProcessor {

    @Autowired
    private EncryptionProperties config;

    private static final Logger logger = LoggerFactory.getLogger(EncryptionDataProcessor.class);

    private Cipher encCipher;
    private Cipher decCipher;

    private boolean initialized;

    public EncryptionDataProcessor() {
    }

    public EncryptionDataProcessor(EncryptionProperties config) {
        this.config = config;
    }

    @PostConstruct
    public void init() {

        try {
            initialized = false;

            if (config.isClientDecryptionEnabled()) {
                logger.info("Client decryption setting is enabled, API will not process encryption / decryption.");
                return;
            }

            doInitCiphers();
            initialized = true;
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException ex) {
            logger.error("Could not initialize encryption algorithm.", ex);
        } catch (InvalidKeyException | InvalidKeySpecException ex) {
            logger.error("Invalid ecryption key", ex);
        }

    }

    private void doInitCiphers() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidKeySpecException, InvalidAlgorithmParameterException {

        //init both encryption and decryption ciphers
        encCipher = CipherBuilder.builder(config).buildForEncryption();
        decCipher = CipherBuilder.builder(config).buildForDecryption();
    }


    @Override
    public ConfigurationElement processWriteConfig(String operationName, ConfigurationElement original) {
        return doComputePropertyReplacement(original, v -> {
            try {
                return Base64.getEncoder().encodeToString(encCipher.doFinal(v.getBytes()));
            } catch (IllegalBlockSizeException | BadPaddingException ex) {
                logger.error("Could not process encryption", ex);
                return v;
            }
        });
    }

    @Override
    public ConfigurationElement processReadConfig(String operationName, ConfigurationElement original) {
        return doComputePropertyReplacement(original, v -> {
            try {
                return new String(decCipher.doFinal(Base64.getDecoder().decode(v)));
            } catch (IllegalBlockSizeException | BadPaddingException ex) {
                logger.error("Could not process decryption", ex);
                return v;
            }
        });
    }


    @Override
    public DocumentData processReadDocument(String operationName, DocumentData original) {
        return processStreams(original, decCipher);
    }

    @Override
    public DocumentData processWriteDocument(String operationName, DocumentData original) {
        return processStreams(original, encCipher);
    }

    private DocumentData processStreams(DocumentData original, Cipher cipher) {
        DefaultDocumentData ret = new DefaultDocumentData();
        ret.setDocument(original.getDocument());
        ret.setData(new CipherInputStream(original.getData(), cipher));
        return ret;
    }


    private ConfigurationElement doComputePropertyReplacement(ConfigurationElement original, Function<String, String> replacementFunction) {

        if (config.isClientDecryptionEnabled()) {
            return original;
        }

        if (!initialized) {
            logger.warn("Bypassing encryption as it has invalid configuration!");
            return original;
        }

        //encrypt all the properties.
        ConfigurationElement.PropertiesType replaced = new DefaultConfigurationElement.DefaultPropertiesType();

        for(Map.Entry<String, String> prop :  original.getProperties().entrySet()) {
            try {
                String str = replacementFunction.apply(prop.getValue());

                String key = replacementFunction.apply(prop.getKey());

                if (StringUtils.hasText(key) && str.equals(prop.getKey())) {
                    logger.error("Could not encrypt/decrypt property {}, keeping original value...", key);
                }

                if (StringUtils.hasText(str) && str.equals(prop.getValue())) {
                    logger.error("Could not encrypt/decrypt property {}, keeping original value...", key);
                }

                replaced.put(key, str);
            } catch (Exception ex) {
                logger.error("Could not encrypt property: {} keeping original value...", prop.getKey(), ex);
                replaced.put(prop.getKey(), prop.getValue());
            }
        }

        ConfigurationElement ret = new DefaultConfigurationElement();
        ret.setProperties(replaced);
        ret.setImports(original.getImports());
        ret.setDocuments(original.getDocuments());
        ret.setVersion(original.getVersion());
        ret.setEnvironment(original.getEnvironment());
        ret.setApplication(original.getApplication());

        return ret;
    }

}
