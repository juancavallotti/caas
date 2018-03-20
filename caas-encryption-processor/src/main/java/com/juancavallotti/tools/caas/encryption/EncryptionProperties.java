package com.juancavallotti.tools.caas.encryption;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "encryption")
public class EncryptionProperties {

    public static final String MAC_KEY_ALG = "HmacSHA256";
    public static final String WRAPPING_KEY_ALG = "Blowfish";

    private String algoritm;
    private String encryptionKey;
    private String keyPassword;
    private String keystoreLocation;
    private String keystorePassword;
    private String keyAlias;

    //extension API
    private boolean extensionApiEnabled = true;

    private boolean clientDecryptionEnabled = false;

    private String macKeyAlias;
    private String macKeyPassword;
    private String wrapKeyAlias;
    private String wrapKeyPassword;



    public String getAlgoritm() {
        return algoritm;
    }

    public void setAlgoritm(String algoritm) {
        this.algoritm = algoritm;
    }

    public String getEncryptionKey() {
        return encryptionKey;
    }

    public void setEncryptionKey(String encryptionKey) {
        this.encryptionKey = encryptionKey;
    }

    public String getKeystoreLocation() {
        return keystoreLocation;
    }

    public void setKeystoreLocation(String keystoreLocation) {
        this.keystoreLocation = keystoreLocation;
    }

    public String getKeystorePassword() {
        return keystorePassword;
    }

    public void setKeystorePassword(String keystorePassword) {
        this.keystorePassword = keystorePassword;
    }

    public String getKeyAlias() {
        return keyAlias;
    }

    public void setKeyAlias(String keyAlias) {
        this.keyAlias = keyAlias;
    }

    public String getKeyPassword() {
        return keyPassword;
    }

    public void setKeyPassword(String keyPassword) {
        this.keyPassword = keyPassword;
    }

    public boolean isExtensionApiEnabled() {
        return extensionApiEnabled;
    }

    public void setExtensionApiEnabled(boolean extensionApiEnabled) {
        this.extensionApiEnabled = extensionApiEnabled;
    }

    public String getMacKeyAlias() {
        return macKeyAlias;
    }

    public void setMacKeyAlias(String macKeyAlias) {
        this.macKeyAlias = macKeyAlias;
    }

    public String getMacKeyPassword() {
        return macKeyPassword;
    }

    public void setMacKeyPassword(String macKeyPassword) {
        this.macKeyPassword = macKeyPassword;
    }

    public String getWrapKeyAlias() {
        return wrapKeyAlias;
    }

    public void setWrapKeyAlias(String wrapKeyAlias) {
        this.wrapKeyAlias = wrapKeyAlias;
    }

    public String getWrapKeyPassword() {
        return wrapKeyPassword;
    }

    public void setWrapKeyPassword(String wrapKeyPassword) {
        this.wrapKeyPassword = wrapKeyPassword;
    }

    public boolean isClientDecryptionEnabled() {
        return clientDecryptionEnabled;
    }

    public void setClientDecryptionEnabled(boolean clientDecryptionEnabled) {
        this.clientDecryptionEnabled = clientDecryptionEnabled;
    }
}
