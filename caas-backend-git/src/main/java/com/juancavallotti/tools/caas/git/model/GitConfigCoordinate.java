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

        buildParents(ret, globals, repo);

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

    private void buildParents(ConfigurationElement element, List<GitConfigCoordinate> globals, List<GitConfigCoordinate> repo) {

        //simply remove all apps that have the same name, so we dont have strange combinations.
        //we only want globals that are the same version and environment too, so the rule is
        //different name AND same version AND same environment
        List<ConfigCoordinate> globalParents = globals.stream()
                .filter(p -> !getApplication().equals(p.getApplication())
                        && getEnvironment().equals(p.getEnvironment())
                        && getVersion().equals(p.getVersion()))
                .collect(Collectors.toList());

        //we add all the effective globals.
        final List<ConfigCoordinate> parents = new LinkedList<>(globalParents);



        //we read the configuration.
        environmentSettings.getParents().forEach(p -> {

            //this way we're sure that we're avoiding the user to repeat obvious information.
            String app = Optional.ofNullable(p.getApplication()).orElseThrow(() -> new IllegalArgumentException("Missing parent name! Please check settings."));
            String ver = Optional.ofNullable(p.getVersion()).orElse(getVersion());
            String env = Optional.ofNullable(p.getEnvironment()).orElse(getEnvironment());

            //search it in the repo, if it is there, we'll add it.
            GitConfigCoordinate found = repo.stream().filter(coord -> {
                return app.equalsIgnoreCase(coord.getApplication())
                        && ver.equalsIgnoreCase(coord.getVersion())
                        && env.equalsIgnoreCase(coord.getEnvironment());

            }).findAny().orElse(null);

            if (found != null) {
                if (!parents.contains(found)) {
                    parents.add(found);
                }
            } else {
                logger.warn("Parent in configuration [app: {}, ver: {}, env: {}] does not exist in repository!!", app, ver, env);
            }
        });

        //if I am in the global and one of my parents is global too, then I need to warn about possible circular dependency!!
        if (globals.contains(element)) {
            parents.forEach(p -> {
                if (globals.contains(p)) logger.warn("Possible circular dependency found!! {} and {} are mutual parents!", p.print(), element.print());
            });
        }

        element.setParents(parents);
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
