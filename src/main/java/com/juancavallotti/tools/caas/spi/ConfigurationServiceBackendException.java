package com.juancavallotti.tools.caas.spi;

public class ConfigurationServiceBackendException extends Exception {

    private final ExceptionCause causeType;

    public static enum ExceptionCause {
        VALIDATION,
        INTERNAL_ERROR,
        ENTITY_NOT_FOUND
    }

    ConfigurationServiceBackendException(String message, Throwable cause, ExceptionCause causeType) {
        super(message, cause);
        this.causeType = causeType;
    }

    public static ConfigurationServiceBackendExceptionBuilder builder() {
        return new ConfigurationServiceBackendExceptionBuilder();
    }

    public ExceptionCause getCauseType() {
        return causeType;
    }
}

