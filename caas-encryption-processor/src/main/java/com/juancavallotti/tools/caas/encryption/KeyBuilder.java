package com.juancavallotti.tools.caas.encryption;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.Key;
import java.security.KeyStore;

public class KeyBuilder {

    private final EncryptionProperties config;

    private static final Logger logger = LoggerFactory.getLogger(KeyBuilder.class);

    private KeyBuilder(EncryptionProperties config) {
        this.config = config;
    }

    public static KeyBuilder builder(EncryptionProperties settings) {
        return new KeyBuilder(settings);
    }

    public Key buildKeySpec() {
        if (StringUtils.isEmpty(config.getKeystoreLocation())) {
            return specFromPassword();
        } else {
            return specFromKeyStore();
        }
    }

    private Key specFromPassword() {
        return new SecretKeySpec(getBytes(config.getEncryptionKey()), "AES");
    }

    private Key specFromKeyStore() {
        try {
            return doLoadFromKeyStore();
        } catch (Exception ex) {
            logger.error("Could not Load key from keyStore", ex);
            throw new RuntimeException(ex);
        }
    }

    private Key doLoadFromKeyStore() throws Exception {

        InputStream is = loadFromClassPathOrFile(config.getKeystoreLocation());
        char[] keyStorePassword = getChars(config.getKeystorePassword());
        char[] keyPassword = getChars(config.getKeyPassword());

        KeyStore ks = KeyStore.getInstance("JCEKS");
        ks.load(is, keyStorePassword);

        Key key = ks.getKey(config.getKeyAlias(), keyPassword);

        if (key == null) {
            throw new RuntimeException("Encryption key not found in keystore, verify key alias...");
        }

        return key;
    }

    private byte[] getBytes(String str) {
        if (str == null) {
            return new byte[0];
        } else {
            return str.getBytes();
        }
    }

    private char[] getChars(String str) {
        if (str == null) {
            return new char[0];
        } else {
            return str.toCharArray();
        }
    }

    private static InputStream loadFromClassPathOrFile(String fileName) throws IOException {
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);

        if (is != null) {
            return is;
        }

        logger.debug("Resource {} not found in classpath, looking in filesystem.", fileName);

        File f = new File(fileName);

        if (f.exists()) {
            return new FileInputStream(f);
        }

        logger.error("Resource {} not found in classpath or filesystem!!", fileName);
        throw new FileNotFoundException(fileName);
    }
}
