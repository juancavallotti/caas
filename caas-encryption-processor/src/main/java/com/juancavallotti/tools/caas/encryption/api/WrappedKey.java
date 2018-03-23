package com.juancavallotti.tools.caas.encryption.api;

public class WrappedKey {

    private String algorithm;
    private String encodedKey;
    private String macSignature;
    private String parameters;

    public WrappedKey() {
    }

    /**
     * Build wrapped key along with information that is useful for the client to decrypt the data provided by this
     * service.
     *
     * @param algorithm the name of the algoritm of the key.
     * @param encodedKey the wrapped key encoded in base 64.
     * @param macSignature the signature of the key.
     * @param algParameters the algorithm parameters.
     */
    public WrappedKey(String algorithm, String encodedKey, String macSignature, String algParameters) {
        this.algorithm = algorithm;
        this.encodedKey = encodedKey;
        this.macSignature = macSignature;
        this.parameters = algParameters;
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

    public String getParameters() {
        return parameters;
    }

    public void setParameters(String parameters) {
        this.parameters = parameters;
    }
}
