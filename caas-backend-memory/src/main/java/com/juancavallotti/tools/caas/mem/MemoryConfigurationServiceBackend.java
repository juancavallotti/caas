package com.juancavallotti.tools.caas.mem;

import com.juancavallotti.tools.caas.api.*;
import com.juancavallotti.tools.caas.mem.model.ModelUtils;
import com.juancavallotti.tools.caas.spi.ConfigurationServiceBackend;
import com.juancavallotti.tools.caas.spi.ConfigurationServiceBackendException;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

public class MemoryConfigurationServiceBackend implements ConfigurationServiceBackend {

    private static final Logger logger = LoggerFactory.getLogger(MemoryConfigurationServiceBackend.class);

    private Set<ConfigurationElement> repository;
    private Map<ConfigCoordinate, Map<String, byte[]>> documentRepository;

    @PostConstruct
    public void init() {
        logger.warn("Initializing In-Memory backend, NOTE: use it only for demo purposes!!!");
        repository = new HashSet<>();
        documentRepository = new HashMap<>();
    }

    @Override
    public List<ConfigCoordinate> listConfigurations() throws ConfigurationServiceBackendException {
        return repository.stream()
                .map(ModelUtils::toCoordinate)
                .collect(Collectors.toList());
    }

    @Override
    public ConfigCoordinate createNewConfiguration(ConfigurationElement element) throws ConfigurationServiceBackendException {

        //we need to validate

        if (element.getParents() != null) {
            for(ConfigCoordinate parent : element.getParents()) {
                if (!repository.contains(parent)) {
                    throw configNotFound(parent);
                }
            }
        }

        //clean if documents were provided.
        element.setDocuments(null);

        ConfigurationElement ret = ModelUtils.toConfiguration(element);

        repository.add(ret);

        return ret;
    }

    @Override
    public Document setDocument(ConfigCoordinate coordinate, String documentName, String contentType, InputStream documentData) throws ConfigurationServiceBackendException {
        try {
            Map<String, byte[]> appRepo = documentRepository.getOrDefault(coordinate, new HashMap<>());
            Document doc = ModelUtils.toDocument(documentName, contentType);
            byte[] data = IOUtils.toByteArray(documentData);

            //store the document
            appRepo.put(documentName, data);
            //this might have been the first invocation
            if (!documentRepository.containsKey(coordinate)) {
                documentRepository.put(coordinate, appRepo);
            }

            //need to update the coordinate
            ConfigurationElement element = findInRepo(coordinate).orElseThrow(() -> configNotFound(coordinate));

            ModelUtils.addDocument(element, doc);

            return doc;
        } catch (IOException ex) {
            logger.error("Got exception while reading Stream", ex);
            throw internalError(ex);
        }
    }

    @Override
    public DocumentData getDocumentData(ConfigCoordinate coordinate, String documentName) throws ConfigurationServiceBackendException {

        ConfigurationElement config = findInRepo(coordinate).orElseThrow(() -> configNotFound(coordinate));

        Map<String, byte[]> appRepo = documentRepository.getOrDefault(coordinate, new HashMap<>());

        if (appRepo.isEmpty()) {
            throw configNotFound(coordinate);
        }

        byte[] data = appRepo.get(documentName);

        if (data == null) {
            throw documentNotFound(documentName);
        }

        return new DocumentData() {
            @Override
            public Document getDocument() {
                return ModelUtils.findDocument(config, documentName)
                        .orElseThrow(() -> new RuntimeException("This should never happen"));
            }

            @Override
            public InputStream getData() {
                return new ByteArrayInputStream(data);
            }
        };

    }

    @Override
    public ConfigurationElement findConfiguration(String application, String version, String env) throws ConfigurationServiceBackendException {
        return findInRepo(application, version, env)
                .orElseThrow(() -> configNotFound(ModelUtils.toCoordinate(application, version, env)));
    }

    @Override
    public ConfigurationElement replaceConfiguration(ConfigurationElement entity) throws ConfigurationServiceBackendException {

        ConfigurationElement original = findInRepo(entity)
                .orElseThrow(() -> configNotFound(entity));


        //remove the documents
        documentRepository.remove(original);

        //store :D
        return (ConfigurationElement) createNewConfiguration(entity);
    }

    @Override
    public ConfigurationElement patchConfiguration(ConfigurationElement entity) throws ConfigurationServiceBackendException {

        //this would leave the existing element half populated on a failure.
        //is so better to store a copy of it before updating.

        //retrieve config
        ConfigurationElement existing = findInRepo(entity)
                .orElseThrow(() -> configNotFound(entity));

        repository.add(ModelUtils.toConfiguration(existing));

        //merge the properties.
        if (existing.getProperties() == null) {
            existing.setProperties(new DefaultConfigurationElement.DefaultPropertiesType());
        }
        //override or create new
        existing.getProperties().putAll(entity.getProperties());

        //we're done
        if (entity.getParents() == null) {
            return existing;
        }

        if (existing.getParents() == null) {
            //init the list if not there.
            existing.setParents(new LinkedList<>());
        }

        for (ConfigCoordinate parent : entity.getParents()) {
            if (!existing.getParents().contains(parent)) {
                existing.getParents().add(ModelUtils.toCoordinate(parent));
            }
        }

        return existing;
    }

    @Override
    public ConfigurationElement promoteConfiguration(ConfigCoordinate coordinate, String targetEnvironment) throws ConfigurationServiceBackendException {

        //the promote task is easy.
        // We just create a copy of the model and store a copy of the documents.
        ConfigurationElement existing = findInRepo(coordinate)
                .orElseThrow(() -> configNotFound(coordinate));

        //copy
        ConfigurationElement copy = ModelUtils.toConfiguration(existing);

        //change the environment
        copy.setEnvironment(targetEnvironment);

        //if the config already exists, fail!
        if (repository.contains(copy)) {
            throw exceptionWithDescriptionAndCause("Configuration already exists: " + copy.print(),
                    null, ConfigurationServiceBackendException.ExceptionCause.VALIDATION);
        }

        //otherwise duplicate the documents, and we're done //we don't care to copy
        //the data.
        documentRepository.put(ModelUtils.toCoordinate(copy), new HashMap<>(documentRepository.get(coordinate)));

        return copy;
    }

    @Override
    public <T extends ConfigurationElement> List<T> createNewVersion(String appName, String version, String targetVersion) throws ConfigurationServiceBackendException {

        //this one is more interesting, this copies the entire thing for as many environments we find.
        List<ConfigurationElement> configs = repository.stream()
                .filter(app -> Objects.equals(appName, app.getApplication()) && Objects.equals(version, app.getVersion()))
                .collect(Collectors.toList());

        List<T> newElements = new LinkedList<>();
        Map<ConfigCoordinate, Map<String, byte[]>> newData = new HashMap<>();

        //now we do some magic.
        for (ConfigurationElement config : configs) {
            //full copy.
            T copy = (T) ModelUtils.toConfiguration(config);
            //change the version.
            if (repository.contains(copy)) {
                throw exceptionWithDescriptionAndCause("Configuration already exists: " + copy.print(),
                        null, ConfigurationServiceBackendException.ExceptionCause.VALIDATION);
            }

            //proceed to add
            newElements.add(copy);
            newData.put(ModelUtils.toCoordinate(copy), new HashMap<>(documentRepository.get(config)));
        }

        repository.addAll(newElements);
        documentRepository.putAll(newData);

        return Collections.unmodifiableList(newElements);
    }

    private Optional<ConfigurationElement> findInRepo(String app, String version, String env) {
        return findInRepo(ModelUtils.toCoordinate(app, version, env));
    }

    private Optional<ConfigurationElement> findInRepo(ConfigCoordinate coordinate) {
        return repository.stream()
                .filter(item -> Objects.equals(item, coordinate))
                .findFirst();
    }

    private ConfigurationServiceBackendException configNotFound(ConfigCoordinate c) {
        return exceptionWithDescriptionAndCause("Config not found: " + c.print(), null, ConfigurationServiceBackendException.ExceptionCause.ENTITY_NOT_FOUND);
    }

    private ConfigurationServiceBackendException documentNotFound(String documentName) {
        return exceptionWithDescriptionAndCause("Document not found: " + documentName, null, ConfigurationServiceBackendException.ExceptionCause.ENTITY_NOT_FOUND);
    }

    private ConfigurationServiceBackendException internalError(Throwable cause) {
        return exceptionWithDescriptionAndCause(cause.getMessage(), cause, ConfigurationServiceBackendException.ExceptionCause.INTERNAL_ERROR);
    }

    private ConfigurationServiceBackendException exceptionWithDescriptionAndCause(String description, Throwable cause, ConfigurationServiceBackendException.ExceptionCause causeType) {
        return ConfigurationServiceBackendException.builder()
                .setCauseType(causeType)
                .setCause(cause)
                .setMessage(description)
                .build();
    }


}
