package com.juancavallotti.tools.caas.spi;

public class ConfigurationServiceBackendException extends Exception {

    private final ExceptionCause causeType;

    public static enum ExceptionCause {
        VALIDATION,
        INTERNAL_ERROR,
        ENTITY_NOT_FOUND,
        OPERATION_NOT_SUPPORTED
    }

    ConfigurationServiceBackendException(String message, Throwable cause, ExceptionCause causeType) {
        super(message, cause);
        this.causeType = causeType;
    }

    //utility methods
    public static ConfigurationServiceBackendExceptionBuilder builder() {
        return new ConfigurationServiceBackendExceptionBuilder();
    }

    public static ConfigurationServiceBackendException notSupported() throws ConfigurationServiceBackendException {
        return builder()
                .setMessage("Operation not implemented")
                .setCauseType(ExceptionCause.OPERATION_NOT_SUPPORTED)
                .build();
    }

    public ExceptionCause getCauseType() {
        return causeType;
    }
}

