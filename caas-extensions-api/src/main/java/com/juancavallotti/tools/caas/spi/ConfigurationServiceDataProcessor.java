package com.juancavallotti.tools.caas.spi;

import com.juancavallotti.tools.caas.api.ConfigurationElement;
import com.juancavallotti.tools.caas.api.DocumentData;

/**
 * SPI to define how a the configuration process the data, this could be of several uses such as encrypting the data,
 * changing the shape of the data for storage, and other uses.
 */
public interface ConfigurationServiceDataProcessor {

    /**
     * Informs whether this service provider is enabled by default or not.
     * @return true if the service is enabled by default, otherwise it will have to be manually specified by configuration.
     */
    default boolean enabledByDefault() {
        return false;
    }

    /**
     * Returns the order in which this implementation should be processed, lower number means higher priority, equal
     * numbers mean same priority and, in that case, the execution order is undefined.
     * @return
     */
    default int getOrder() {
        return 5;
    }


    /**
     * Process a particular configuration element before calling a write operation within the service.
     * @param original the configuration element before processing.
     * @return the modified configuration element.
     */
    default ConfigurationElement processWriteConfig(String operationName, ConfigurationElement original) {
        return original;
    }


    /**
     * Process a particular configuration element before calling a read operation within the service.
     * @param original the configuration element before processing.
     * @return the modified configuration element.
     */
    default ConfigurationElement processReadConfig(String operationName, ConfigurationElement original) {
        return original;
    }

    /**
     * Process a particular data element before calling a write operation within the service.
     * @param original the document element before processing.
     * @return the modified configuration element.
     */
    default DocumentData processWriteDocument(String operationName, DocumentData original) {
        return original;
    }

    /**
     * Process a particular data element before calling a read operation within the service.
     * @param original the document element before processing.
     * @return the modified configuration element.
     */
    default DocumentData processReadDocument(String operationName, DocumentData original) {
        return original;
    }

}
