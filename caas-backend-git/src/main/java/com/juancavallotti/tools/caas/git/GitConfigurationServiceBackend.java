package com.juancavallotti.tools.caas.git;

import com.juancavallotti.tools.caas.api.*;
import com.juancavallotti.tools.caas.git.model.GitConfigCoordinate;
import com.juancavallotti.tools.caas.git.model.GitRepositoryModel;
import com.juancavallotti.tools.caas.spi.ConfigurationServiceBackend;
import com.juancavallotti.tools.caas.spi.ConfigurationServiceBackendException;
import org.eclipse.jgit.api.Git;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GitConfigurationServiceBackend implements ConfigurationServiceBackend {

    private static final Logger logger = LoggerFactory.getLogger(GitConfigurationServiceBackend.class);

    @Value("${gitbackend.repoUrl}")
    private String repoBackend;

    @Value("${gitbackend.branch:master}")
    private String branch;

    @Value("${gitbackend.localPath}")
    private String localPath;

    private Git git;
    private File repoDir;
    private RepositoryWatcher watcher;


    public GitConfigurationServiceBackend() {
        logger.info(getServiceName());
    }

    private GitRepositoryModel model;

    @PostConstruct
    public void init() {
        logger.info("Loaded Git Configuration Service, backend url: {}, local repo path: {}", repoBackend, localPath);
        try {
            logger.debug("Cloning or opening git repository...");
            GitRepository repo = new GitRepository(repoBackend, localPath, branch);
            git = repo.buildGit();

            if (git == null) {
                logger.error("Error while connecting to GIT repository. Check settings.");
                model = new GitRepositoryModel(Collections.emptyList(), Collections.emptyList());
                return;
            }

            repoDir = git.getRepository().getWorkTree();

            logger.debug("Checking out specific branch {}", branch);

            repo.checkoutBranch(git);

            //print the current git status.
            repo.printStatus(git, logger);

            logger.debug("Parsing directory...");
            //build the model.
            model = GitRepositoryParser.buildModel(repoDir);

            watcher = new RepositoryWatcher().watch(repoDir.toPath(),
                    () -> {
                        logger.info("Repository has changed, will dynamically reload the model...");
                        GitRepositoryModel repoModel = GitRepositoryParser.buildModel(repoDir);
                        logger.info("Model reload complete...");
                        model = repoModel;
                    });

        } catch (Exception ex) {
            logger.error("Error while connecting to repository. ", ex);
        }

    }

    @PreDestroy
    public void shutdown() {
        logger.debug("Shutting down... will try to clean up.");
        watcher.shutdown();
    }


    @Override
    public List<ConfigCoordinate> listConfigurations() {
        return model.listConfigs();
    }


    @Override
    public DocumentData getDocumentData(ConfigCoordinate coordinate, String documentName) throws ConfigurationServiceBackendException {

        GitConfigCoordinate gitCoordinate = model.findConfiguration(coordinate)
                .orElseThrow(this::coordinateNotFound);

        return gitCoordinate.documentData(documentName).orElseThrow(this::documentNotFound);
    }

    @Override
    public ConfigurationElement findConfiguration(String application, String version, String environment) throws ConfigurationServiceBackendException {

        ConfigurationElement ret = model.findConfigurationElement(application, version, environment)
                .orElseThrow(this::coordinateNotFound);
        return ret;
    }

    public ConfigurationServiceBackendException coordinateNotFound() {
        return ConfigurationServiceBackendException.builder()
                .setMessage("Configuration not found.")
                .setCauseType(ConfigurationServiceBackendException.ExceptionCause.ENTITY_NOT_FOUND)
                .build();
    }

    public ConfigurationServiceBackendException documentNotFound() {
        return ConfigurationServiceBackendException.builder()
                .setMessage("Document not found.")
                .setCauseType(ConfigurationServiceBackendException.ExceptionCause.ENTITY_NOT_FOUND)
                .build();
    }

}
