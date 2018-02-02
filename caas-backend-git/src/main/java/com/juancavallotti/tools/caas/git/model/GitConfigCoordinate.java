package com.juancavallotti.tools.caas.git.model;

import com.juancavallotti.tools.caas.api.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class GitConfigCoordinate extends DefaultConfigCoordinate {

    private final String documentFolderMapping;

    private final Properties props;

    private final Map<String, GitDocument> documents;

    public GitConfigCoordinate(String documentFolderMapping, Properties props, Map<String, GitDocument> documents) {
        this.documentFolderMapping = documentFolderMapping;
        this.props = Optional.of(props).orElse(new Properties());
        this.documents = Optional.of(documents).orElse(new HashMap<>());
    }

    ConfigurationElement buildElement(List<GitConfigCoordinate> globals) {

        DefaultConfigurationElement ret = new DefaultConfigurationElement();

        ret.setApplication(getApplication());
        ret.setVersion(getVersion());
        ret.setEnvironment(getEnvironment());

        //build the properties.
        LinkedList<ConfigCoordinate> parents = new LinkedList<>(globals);

        //TODO - Read other parents by configuration.
        ret.setParents(Collections.unmodifiableList(parents));

        //read the documents.
        ret.setDocuments(Collections.unmodifiableList(new LinkedList<>(documents.values())));

        ret.setProperties(new DefaultConfigurationElement.DefaultPropertiesType());

        Map<String, String> props = new HashMap<>();
        this.props.entrySet().forEach(entry -> props.put(entry.getKey().toString(), entry.getValue().toString()));
        ret.getProperties().putAll(Collections.unmodifiableMap(props));

        return ret;
    }

    public Optional<DocumentData> documentData(String documentName) {
        GitDocument doc = documents.get(documentName);
        if (doc == null) {
            return Optional.empty();
        }

        return Optional.of(new DocumentData() {
            @Override
            public Document getDocument() {
                return doc;
            }

            @Override
            public InputStream getData() {
                try {
                    return doc.readContents();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
    }
}
