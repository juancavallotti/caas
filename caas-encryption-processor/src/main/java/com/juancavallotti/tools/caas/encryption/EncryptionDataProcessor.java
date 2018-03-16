package com.juancavallotti.tools.caas.encryption;

import com.juancavallotti.tools.caas.api.*;
import com.juancavallotti.tools.caas.spi.ConfigurationServiceDataProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
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
            doInitCiphers();
            initialized = true;
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException ex) {
            logger.error("Could not initialize encryption algorithm.");
        } catch (InvalidKeyException | InvalidKeySpecException ex) {
            logger.error("Invalid ecryption key", ex);
        }

    }

    private void doInitCiphers() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidKeySpecException, InvalidAlgorithmParameterException {

        //init both encryption and decryption ciphers
        encCipher = Cipher.getInstance(config.getAlgoritm());
        decCipher = Cipher.getInstance(config.getAlgoritm());
        SecretKeySpec encryptionKey = retrieveKey();
        IvParameterSpec initVector = buildInitVector();

        encCipher.init(Cipher.ENCRYPT_MODE, encryptionKey, initVector);
        decCipher.init(Cipher.DECRYPT_MODE, encryptionKey, initVector);
    }

    private SecretKeySpec retrieveKey() throws NoSuchAlgorithmException, InvalidKeySpecException {
        return new SecretKeySpec(getChars(config.getEncryptionKey()), "AES");
    }

    private IvParameterSpec buildInitVector() throws NoSuchAlgorithmException {
        return new IvParameterSpec(config.getEncryptionKey().getBytes());
    }

    private byte[] getChars(String str) {
        if (str == null) {
            return new byte[0];
        } else {
            return str.getBytes();
        }
    }

    @Override
    public ConfigurationElement processWriteConfig(String operationName, ConfigurationElement original) {
        return doComputePropertyReplacement(original, v -> {
            try {
                return Base64.getEncoder().encodeToString(encCipher.doFinal(v.getValue().getBytes()));
            } catch (IllegalBlockSizeException | BadPaddingException ex) {
                logger.error("Could not encrypt property, keeping original value...", v.getKey(), ex);
                return v.getValue();
            }
        });
    }

    @Override
    public ConfigurationElement processReadConfig(String operationName, ConfigurationElement original) {
        return doComputePropertyReplacement(original, v -> {
            try {
                return new String(decCipher.doFinal(Base64.getDecoder().decode(v.getValue())));
            } catch (IllegalBlockSizeException | BadPaddingException ex) {
                logger.error("Could not decrypt property, keeping original value...", v.getKey(), ex);
                return v.getValue();
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


    private ConfigurationElement doComputePropertyReplacement(ConfigurationElement original, Function<Map.Entry<String, String>, String> replacementFunction) {
        if (!initialized) {
            logger.warn("Bypassing encryption as it has invalid configuration!");
            return original;
        }

        //encrypt all the properties.
        ConfigurationElement.PropertiesType replaced = new DefaultConfigurationElement.DefaultPropertiesType();

        for(Map.Entry<String, String> prop :  original.getProperties().entrySet()) {
            try {
                String str = replacementFunction.apply(prop);
                replaced.put(prop.getKey(), str);
            } catch (Exception ex) {
                logger.error("Could not encrypt property: {} keeping original value...", prop.getKey(), ex);
                replaced.put(prop.getKey(), prop.getValue());
            }
        }

        original.setProperties(replaced);

        return original;
    }

}
