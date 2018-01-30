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

        //


        return null;
    }

    @Override
    public ConfigurationElement promoteConfiguration(ConfigCoordinate coordinate, String targetEnvironment) throws ConfigurationServiceBackendException {
        return null;
    }

    @Override
    public <T extends ConfigurationElement> List<T> createNewVersion(String appName, String version, String targetVersion) throws ConfigurationServiceBackendException {
        return null;
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
