package com.juancavallotti.tools.caas.encryption;

import com.juancavallotti.tools.caas.encryption.api.WrappedKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.Key;
import java.security.KeyStore;
import java.util.Base64;
import java.util.Optional;

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

    public WrappedKey wrapKey(String keyAlias, String keyPassword) {
        try {
            return doWrapKey(keyAlias, getChars(keyPassword));
        } catch (SecurityException ex) {
            logger.error("Error while wrapping key with alias {}", keyAlias, ex);
            throw new RuntimeException(ex);
        } catch (Exception ex) {
            logger.error("Unknown Error while wrapping key with alias {}", keyAlias, ex);
            throw new RuntimeException(ex);
        }
    }

    private WrappedKey doWrapKey(String keyAlias, char[] keyPassword) throws Exception {

        //retrieve the key to wrap.
        Optional<Key> keyToWrap = doLoadFromKeyStore(keyAlias, keyPassword);

        if (!keyToWrap.isPresent()) {
            logger.error("Specified keyAlias {} not found in keystore", keyAlias);
            return null;
        }

        //retrieve the wrapping key.
        Optional<Key> wrappingKey = doLoadFromKeyStore(config.getWrapKeyAlias(), getChars(config.getWrapKeyPassword()));

        if (!wrappingKey.isPresent()) {
            logger.error("Wrapping key with alias {} not found on keystore", config.getWrapKeyAlias());
            return null;
        }

        Optional<Key> macKey = doLoadFromKeyStore(config.getMacKeyAlias(), getChars(config.getMacKeyPassword()));

        if (!macKey.isPresent()) {
            logger.error("MAC key with alias {} not found on keystore", config.getMacKeyAlias());
        }

        //init the cipher.
        Cipher cipher = Cipher.getInstance(EncryptionProperties.WRAPPING_KEY_ALG);
        cipher.init(Cipher.WRAP_MODE, wrappingKey.get());

        byte[] wrapped = cipher.wrap(keyToWrap.get());

        //generate the mac signature.
        Mac mac = Mac.getInstance(EncryptionProperties.MAC_KEY_ALG);
        mac.init(macKey.get());

        byte[] signature = mac.doFinal(wrapped);


        //convert all to Base64 and return

        String wrappedKeyStr = Base64.getEncoder().encodeToString(wrapped);
        String signatureStr = Base64.getEncoder().encodeToString(signature);

        return new WrappedKey(keyToWrap.get().getAlgorithm(), wrappedKeyStr, signatureStr, config.getAlgorithm());

    }

    private Key specFromPassword() {
        return new SecretKeySpec(getBytes(config.getEncryptionKey()), getKeyAlgorithm(config.getAlgorithm()));
    }

    private Key specFromKeyStore() {
        try {
            return doLoadFromKeyStore(config.getKeyAlias(), getChars(config.getKeyPassword()))
                    .orElseThrow( () -> new RuntimeException("Could not find key " + config.getKeyAlias() + " in trustStore"));
        } catch (Exception ex) {
            logger.error("Could not Load key from keyStore", ex);
            throw new RuntimeException(ex);
        }
    }



    private Optional<Key> doLoadFromKeyStore(String keyAlias, char[] keyPassword) throws Exception {

        InputStream is = loadFromClassPathOrFile(config.getKeystoreLocation());
        char[] keyStorePassword = getChars(config.getKeystorePassword());

        KeyStore ks = KeyStore.getInstance("JCEKS");
        ks.load(is, keyStorePassword);

        Key key = ks.getKey(keyAlias, keyPassword);

        return Optional.ofNullable(key);
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

    private String getKeyAlgorithm(String algString) {

        if (StringUtils.isEmpty(algString)) {
            return null;
        }

        if (algString.contains("/")) {
            return algString.split("/")[0];
        }

        return algString;
    }
}
