package com.juancavallotti.tools.caas.encryption.api;

public class WrappedKey {

    private String algorithm;
    private String encodedKey;
    private String macSignature;

    public WrappedKey() {
    }

    public WrappedKey(String algorithm, String encodedKey, String macSignature) {
        this.algorithm = algorithm;
        this.encodedKey = encodedKey;
        this.macSignature = macSignature;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    public String getEncodedKey() {
        return encodedKey;
    }

    public void setEncodedKey(String encodedKey) {
        this.encodedKey = encodedKey;
    }

    public String getMacSignature() {
        return macSignature;
    }

    public void setMacSignature(String macSignature) {
        this.macSignature = macSignature;
    }
}
