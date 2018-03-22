package com.juancavallotti.tools.caas.encryption;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEParameterSpec;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Set;

/**
 * Class used to build Cipher objects.
 */
public class CipherBuilder {

    private final EncryptionProperties config;

    public static final String DEFAULT_BLOCK_MODE = "CBC";
    public static final String DEFAULT_PADDING = "PKCS5PADDING";

    private static final Set<String> IV_OP_MODES = Set.of("CBC", "CFB", "OFB", "PCBC");

    private static final Logger logger = LoggerFactory.getLogger(CipherBuilder.class);

    private CipherBuilder(EncryptionProperties config) {
        this.config = config;
    }

    public static CipherBuilder builder(EncryptionProperties config) {
        return new CipherBuilder(config);
    }

    public Cipher buildForEncryption() {
        try {
            return doBuildCipher(Cipher.ENCRYPT_MODE, config.getAlgorithm(), config.getMode(), config.getPadding(), getEncryptionKey());
        } catch (Exception ex) {
            logger.error("Could not create encryption cipher", ex);
            throw new RuntimeException(ex);
        }
    }

    public Cipher buildForDecryption() {
        try {
            return doBuildCipher(Cipher.DECRYPT_MODE, config.getAlgorithm(), config.getMode(), config.getPadding(), getEncryptionKey());
        } catch (Exception ex) {
            logger.error("Could not create encryption cipher", ex);
            throw new RuntimeException(ex);
        }
    }

    private Cipher doBuildCipher(int cipherMode, String algorithm, String blockMode, String padding, Key key) throws GeneralSecurityException {

        String algStr = buildAlgorithmString(algorithm, blockMode, padding);

        Cipher cipher = Cipher.getInstance(algStr);

        AlgorithmParameterSpec params = buildAlgorithmParameters(cipher, algStr, key);

        if (params != null) {
            cipher.init(cipherMode, key, params);
        } else {
            cipher.init(cipherMode, key);
        }

        return cipher;
    }

    private Key getEncryptionKey() throws GeneralSecurityException, IOException {
        return KeyBuilder.builder(config).buildKeySpec();
    }

    private String buildAlgorithmString(String algorithm, String blockMode, String padding) {

        if (StringUtils.countOccurrencesOf(algorithm, "/") == 2) {
            logger.debug("Algorithm settings already provides block mode and padding: {}", algorithm);
            //string
            return algorithm;
        }

        if (StringUtils.isEmpty(blockMode)) {
            blockMode = DEFAULT_BLOCK_MODE;
        }

        if (StringUtils.isEmpty(padding)) {
            padding = DEFAULT_PADDING;
        }

        return String.format("%s/%s/%s", algorithm, blockMode, padding);
    }

    private AlgorithmParameterSpec buildAlgorithmParameters(Cipher cipher, String algStr, Key key) throws GeneralSecurityException {

        String[] parts = algStr.split("/");

        if (IV_OP_MODES.contains(parts[1])) {
            //requires an IV Vector
            return buildInitVector(cipher, key);
        }

        if (StringUtils.startsWithIgnoreCase(parts[0], "PBE")) {
            //requires PBEParameter spec
            return buildPBESpec(cipher, key);
        }

        //rest is not supported for now.
        return null;
    }

    private IvParameterSpec buildInitVector(Cipher cipher, Key key) throws GeneralSecurityException {
        byte[] iv = new byte[cipher.getBlockSize()];

        byte[] keyBytes = key.getEncoded();
        //copy the bytes of the key
        for(int i = 0 ; i < iv.length ; i++) {
            iv[i] = i < keyBytes.length ? keyBytes[i] : 0;
        }

        return new IvParameterSpec(iv);
    }

    private PBEParameterSpec buildPBESpec(Cipher cipher, Key key) throws GeneralSecurityException {

        SecureRandom random = SecureRandom.getInstanceStrong();
        random.setSeed(key.getEncoded());
        byte[] salt = new byte[cipher.getBlockSize()];
        random.nextBytes(salt);
        int count = random.nextInt();
        return new PBEParameterSpec(salt, count);

    }

}
