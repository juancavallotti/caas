package com.juancavallotti.tools.caas.spi;

public class ConfigurationServiceBackendExceptionBuilder {
    private String message;
    private Throwable cause;
    private ConfigurationServiceBackendException.ExceptionCause causeType;

    public ConfigurationServiceBackendExceptionBuilder setMessage(String message) {
        this.message = message;
        return this;
    }

    public ConfigurationServiceBackendExceptionBuilder setCause(Throwable cause) {
        this.cause = cause;
        return this;
    }

    public ConfigurationServiceBackendExceptionBuilder setCauseType(ConfigurationServiceBackendException.ExceptionCause causeType) {
        this.causeType = causeType;
        return this;
    }

    public ConfigurationServiceBackendException build() {
        return new ConfigurationServiceBackendException(message, cause, causeType);
    }
}