package com.juancavallotti.tools.caas.mongo;

import com.juancavallotti.tools.caas.api.ConfigCoordinate;
import com.juancavallotti.tools.caas.api.ConfigurationElement;
import com.juancavallotti.tools.caas.api.Document;
import com.juancavallotti.tools.caas.api.DocumentData;
import com.juancavallotti.tools.caas.mongo.model.MongoDocument;
import com.juancavallotti.tools.caas.mongo.model.MongoDocumentData;
import com.juancavallotti.tools.caas.mongo.model.MongoConfigurationElement;
import com.juancavallotti.tools.caas.mongo.repository.ConfigurationRepository;
import com.juancavallotti.tools.caas.mongo.repository.DocumentDataRepository;
import com.juancavallotti.tools.caas.spi.ConfigurationServiceBackend;
import com.juancavallotti.tools.caas.spi.ConfigurationServiceBackendException;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class MongoConfigurationServiceBackend implements ConfigurationServiceBackend {

    private static final Logger logger = LoggerFactory.getLogger(MongoConfigurationServiceBackend.class);

    @Autowired
    private ConfigurationRepository repository;

    @Autowired
    private DocumentDataRepository dataRepository;

    public MongoConfigurationServiceBackend() {
        logger.info(serviceName("Mongo"));
    }

    @Override
    public List<ConfigCoordinate> listConfigurations() {

        logger.debug("Repository class is: {}", repository.getClass().getName());

        return (List) repository.findAllCoordinates();
    }

    @Override
    public ConfigCoordinate createNewConfiguration(ConfigurationElement element) throws ConfigurationServiceBackendException {

        if (element.getDocuments() != null && !element.getDocuments().isEmpty()) {

            throwWithMessageAndCause("Configuration cannot have documents",
                    ConfigurationServiceBackendException.ExceptionCause.VALIDATION);
        }

        MongoConfigurationElement existing = repository
                .findByApplicationIgnoreCaseAndVersionAndEnvironmentIgnoreCase(
                        element.getApplication(),
                        element.getVersion(),
                        element.getEnvironment()
                );

        if (existing != null) {
            throwWithMessageAndCause("Configuration already exists with the same coordinates",
                    ConfigurationServiceBackendException.ExceptionCause.VALIDATION);
        }

        //check the parents exist
        for(ConfigCoordinate parent : element.getParents()) {
            if (findWithCoordinate(parent) == null)
                throwWithMessageAndCause("Parent not found",
                        ConfigurationServiceBackendException.ExceptionCause.VALIDATION);
        }


        repository.save(MongoConfigurationElement.fromConfigurationElement(element));
        return element;
    }

    @Override
    public Document setDocument(ConfigCoordinate coordinate, String documentName, String contentType, InputStream documentData) throws ConfigurationServiceBackendException {

        MongoConfigurationElement config = findWithCoordinate(coordinate);

        if (config == null) {
            throwConfigNotFound();
        }

        //if the document already exists, update the data
        Document doc = findDocument(config, documentName);

        MongoDocumentData data = null;

        //save the data in various repositories
        if (doc == null) {
            data = new MongoDocumentData();
            data.setAppId(config.getId());
            data.setKey(documentName);

            doc = new MongoDocument();
            doc.setKey(documentName);
            doc.setType(contentType);

            //set it into the config
            if (config.getDocuments() == null) {
                config.setDocuments(List.of(doc));
            }
            repository.save(config);
        } else {
            data = dataRepository.findByAppIdAndKey(config.getId(), documentName);
        }

        data.setData(readDocumentData(documentData));

        dataRepository.save(data);

        //store the document
        return doc;
    }

    @Override
    public DocumentData getDocumentData(ConfigCoordinate coordinate, String documentName) throws ConfigurationServiceBackendException {

        MongoConfigurationElement config = findWithCoordinate(coordinate);

        if (config == null) {
            throwConfigNotFound();
        }

        //if the document already exists, update the data
        Document doc = findDocument(config, documentName);

        if (doc == null) {
            throwDocNotFound(documentName);
        }

        MongoDocumentData mongoData = dataRepository.findByAppIdAndKey(config.getId(), documentName);

        if (mongoData == null) {
            throwDocNotFound(documentName + " data");
        }

        //return an annonimous  object, we don't need more than that.
        return new DocumentData() {
            @Override
            public Document getDocument() {
                return doc;
            }

            @Override
            public InputStream getData() {
                return new ByteArrayInputStream(mongoData.getData());
            }
        };
    }

    @Override
    public ConfigurationElement findConfiguration(String application, String environment, String version) throws ConfigurationServiceBackendException {

        MongoConfigurationElement ret = repository.findByApplicationIgnoreCaseAndVersionAndEnvironmentIgnoreCase(application, environment, version);

        if (ret == null) {
            throwConfigNotFound();
        }

        return standardizeProps(ret);
    }

    private ConfigurationElement standardizeProps(MongoConfigurationElement elm) {
        elm.setProperties(elm.getProperties().standardize());
        return elm;
    }


    //utility methdods
    public MongoConfigurationElement findWithCoordinate(ConfigCoordinate coordinate) {
        return repository.findByApplicationIgnoreCaseAndVersionAndEnvironmentIgnoreCase(
                coordinate.getApplication(),
                coordinate.getVersion(),
                coordinate.getEnvironment()
        );
    }

    private void throwConfigNotFound() throws ConfigurationServiceBackendException {

        throwWithMessageAndCause("Config not found", ConfigurationServiceBackendException.ExceptionCause.ENTITY_NOT_FOUND);
    }

    private void throwDocNotFound(String name) throws ConfigurationServiceBackendException {
        throwWithMessageAndCause("Document not found: " + name , ConfigurationServiceBackendException.ExceptionCause.ENTITY_NOT_FOUND);
    }

    private byte[] readDocumentData(InputStream stream) throws ConfigurationServiceBackendException {
        try {
            return IOUtils.toByteArray(stream);
        } catch (IOException ex) {
            throw ConfigurationServiceBackendException.builder()
                    .setMessage("Cannot read stream")
                    .setCause(ex)
                    .setCauseType(ConfigurationServiceBackendException.ExceptionCause.INTERNAL_ERROR)
                    .build();
        }
    }

    private void throwWithMessageAndCause(String message, ConfigurationServiceBackendException.ExceptionCause cause) throws ConfigurationServiceBackendException{
        throw ConfigurationServiceBackendException.builder()
                .setMessage(message)
                .setCauseType(cause)
                .build();
    }

    private Document findDocument(MongoConfigurationElement element, String key) {
        if (element.getDocuments() == null) {
            return null;
        }

        return element.getDocuments()
                .stream()
                .filter(document -> document.getKey().equals(key))
                .findFirst().orElse(null);

    }
}
