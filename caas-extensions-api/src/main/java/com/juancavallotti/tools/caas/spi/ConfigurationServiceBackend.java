package com.juancavallotti.tools.caas.spi;

import com.juancavallotti.tools.caas.api.*;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public interface ConfigurationServiceBackend {

    default String printServiceName() {
        return "CaaS Service Implementation: " + getServiceName();
    }

    default String getServiceName() {
        return getClass().getName();
    }

    /**
     * Return a list of all existing configurations. This is an optional operation.
     * @return
     */
    default List<ConfigCoordinate> listConfigurations() throws ConfigurationServiceBackendException {
        throw ConfigurationServiceBackendException.notSupported();
    }

    /**
     * Create a new configuration in the backend. This is an optional operation.
     * @param element
     * @return
     * @throws ConfigurationServiceBackendException
     */
    default ConfigCoordinate createNewConfiguration(ConfigurationElement element) throws ConfigurationServiceBackendException {
        throw ConfigurationServiceBackendException.notSupported();
    }

    /**
     * Create a new document in the backend for the particular coordinate. This is an optional operation.
     * @param coordinate
     * @param documentName
     * @param contentType
     * @param documentData
     * @return
     * @throws ConfigurationServiceBackendException
     */
    default Document setDocument(ConfigCoordinate coordinate, String documentName, String contentType, InputStream documentData) throws ConfigurationServiceBackendException {
        throw ConfigurationServiceBackendException.notSupported();
    }

    /**
     * Read the information of a particular document in the service.
     * This is a mandatory operation. All backends must support reading documents.
     * @param coordinate
     * @param documentName
     * @return
     * @throws ConfigurationServiceBackendException
     */
    DocumentData getDocumentData(ConfigCoordinate coordinate, String documentName) throws ConfigurationServiceBackendException;

    /**
     * Read a configuration in the configuration service.
     * This is a mandatory operation. All backends must support reading configurations.
     * What would be the purpose of the service otherwise?
     * @param application
     * @param version
     * @param env
     * @return
     * @throws ConfigurationServiceBackendException
     */
    ConfigurationElement findConfiguration(String application, String version, String env) throws ConfigurationServiceBackendException;

    /**
     * Update completely an existing configuration.
     * NOTE: this method must remove documents that are not longer required. If user provides new documents
     * there is the option to ignore them or to throw invalid data exception. So basically, compare the list
     * of existing documents, if there are some missing, then remove them from the documents catalog.
     *
     * This is an optional operation.
     * @param entity the configuration to update.
     * @return The updated configuration.
     * @throws ConfigurationServiceBackendException
     */
    default ConfigurationElement replaceConfiguration(ConfigurationElement entity) throws ConfigurationServiceBackendException {
        throw ConfigurationServiceBackendException.notSupported();
    }

    /**
     * Update diferentially an existing configuration.
     * NOTE: this method must not update any of the existing documents. If user provides documents
     * there is the option to ignore them or to throw invalid data exception.
     *
     * This method just creates new properties, or updates existing ones,
     * also updates the imports, if provided.
     *
     * This is an optional operation.
     * @param entity the configuration to update.
     * @return The updated configuration.
     * @throws ConfigurationServiceBackendException
     */
    default ConfigurationElement patchConfiguration(ConfigurationElement entity) throws ConfigurationServiceBackendException {
        throw ConfigurationServiceBackendException.notSupported();
    }


    /**
     * Promote one configuration to a different environment. This is equivalent to creating a brand new setting.
     * The documents must be copied too.
     *
     * This is an optional operation.
     * @param coordinate
     * @param targetEnvironment
     * @return
     * @throws ConfigurationServiceBackendException
     */
    default ConfigurationElement promoteConfiguration(ConfigCoordinate coordinate, String targetEnvironment) throws ConfigurationServiceBackendException {
        throw ConfigurationServiceBackendException.notSupported();
    }

    /**
     * Copy a particular application in all environments to a new version of this application in All environments, whichever
     * are those environments.
     *
     * This is an optional operation.
     * @param appName
     * @param version
     * @param targetVersion
     * @return
     * @throws ConfigurationServiceBackendException
     */
    default <T extends ConfigurationElement> List<T> createNewVersion(String appName, String version, String targetVersion) throws ConfigurationServiceBackendException {
        throw ConfigurationServiceBackendException.notSupported();
    }


    /**
     * Get the details of the methods implemented by Service Provider. This method returns the intersection of methods
     * defined in this interface (whether they're default or not) and the declared methods in the concrete class.
     *
     * NOTE: This method will not look for class hierarchy, so in order for it to work the expected way, only one level
     * of inheritance will be allowed. The implementation can always override it to provide a more accurate description
     * of the service.
     *
     * @return a list of Strings representing the methods that has been implemented by the Service Provider.
     */
    default List<String> implementedFunctionality() {

        Method[] methods = getClass().getDeclaredMethods();
        Method[] ifaceMethods = ConfigurationServiceBackend.class.getMethods();

        //cache the methods for future use
        Map<String, Method> methodsMap = Arrays.stream(ifaceMethods)
                .collect(Collectors.toMap(m -> m.getName(), m -> m));

        //the intersection of the two will be what we look for.
        Set<String> ifaceMethodsSet = Arrays.stream(ifaceMethods)
                .map(m -> m.getName())
                .collect(Collectors.toSet());

        Set<String> methodsSet = Arrays.stream(methods)
                .map(m -> m.getName())
                .collect(Collectors.toSet());

        methodsSet.retainAll(ifaceMethodsSet);

        return methodsSet.stream()
                .map(m -> methodsMap.get(m).toString())
                .collect(Collectors.toList());
    }

    /**
     * Retrieve the configuration properties specific to this backend implementation. This is for informative purposes
     * only and implementation is completely optional.
     *
     * @return An implementation of {@link BackendProperties} interface.
     */
    default Optional<BackendProperties> backendConfiguration() {
        return Optional.empty();
    }
}
