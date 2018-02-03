package com.juancavallotti.tools.caas.git.model;

import com.juancavallotti.tools.caas.api.*;
import com.juancavallotti.tools.caas.git.model.settings.EnvironmentSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

public class GitConfigCoordinate extends DefaultConfigCoordinate {

    private final File propsFile;

    private final File documentsFolder;

    private final EnvironmentSettings environmentSettings;

    private static final Logger logger = LoggerFactory.getLogger(GitConfigCoordinate.class);

    /**
     * Both folders MUST exist
     * @param settings
     * @param propsFile
     * @param documentsFolder
     */
    public GitConfigCoordinate(EnvironmentSettings settings, File propsFile, File documentsFolder) {
        this.propsFile = propsFile;
        this.documentsFolder = documentsFolder;
        this.environmentSettings = settings;
    }

    ConfigurationElement buildElement(List<GitConfigCoordinate> globals, List<GitConfigCoordinate> repo) {

        DefaultConfigurationElement ret = new DefaultConfigurationElement();

        ret.setApplication(getApplication());
        ret.setVersion(getVersion());
        ret.setEnvironment(getEnvironment());

        //build the properties.
        LinkedList<ConfigCoordinate> parents = new LinkedList<>(globals);

        //TODO - Read other parents by configuration.
        ret.setParents(Collections.unmodifiableList(parents));

        //read the documents.
        File[] docs = documentsFolder.listFiles();

        if (docs == null) {
            docs = new File[0];
        }

        List<GitDocument> documents = Arrays.stream(docs)
                .filter(file -> file.isFile())
                .map(file -> new GitDocument(file)).collect(Collectors.toList());

        ret.setDocuments(Collections.unmodifiableList(documents));

        ret.setProperties(new DefaultConfigurationElement.DefaultPropertiesType());

        Map<String, String> propsMap = readProperties().entrySet()
                .stream()
                .collect(Collectors.toMap(ek -> ek.getKey().toString() , ev -> ev.getValue().toString()));
        ret.getProperties().putAll(Collections.unmodifiableMap(propsMap));

        return ret;
    }

    public Optional<DocumentData> documentData(String documentName) {

        File docFile = new File(documentsFolder.getPath() + File.separator + documentName);

        if (!docFile.exists()) {
            return Optional.empty();
        }

        GitDocument doc = new GitDocument(docFile);

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
                    logger.error("Got exception while trying to read contents of file.", ex);
                    throw new RuntimeException(ex);
                }
            }
        });
    }

    private Properties readProperties() {
        try {
            Properties ret = new Properties();
            ret.load(new FileInputStream(propsFile));
            return ret;
        } catch (IOException ex) {
            return new Properties();
        }
    }
}
