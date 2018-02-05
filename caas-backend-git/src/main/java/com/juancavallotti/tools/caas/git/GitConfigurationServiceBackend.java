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

    public GitConfigurationServiceBackend() {
        logger.info(getServiceName());
    }

    private GitRepositoryModel model;

    private boolean running = true;

    @PostConstruct
    public void init() {
        logger.info("Loaded Git Configuration Service, backend url: {}, local repo path: {}", repoBackend, localPath);
        try {
            repoDir = new File(localPath);

            logger.debug("Cloning or opening git repository...");
            GitRepository repo = new GitRepository(repoBackend, localPath, branch);
            git = repo.buildGit();

            if (git == null) {
                logger.error("Error while connecting to GIT repository. Check settings.");
                return;
            }

            logger.debug("Checking out specific branch {}", branch);

            repo.checkoutBranch(git);

            //print the current git status.
            repo.printStatus(git, logger);

            logger.debug("Parsing directory...");
            //build the model.
            model = GitRepositoryParser.buildModel(repoDir);

            logger.debug("Registering a file system watcher to reload the model.");
            final WatchService watcher = FileSystems.getDefault().newWatchService();

            try {
                final WatchKey key = repoDir.toPath().register(watcher,
                        StandardWatchEventKinds.ENTRY_CREATE,
                        StandardWatchEventKinds.ENTRY_DELETE,
                        StandardWatchEventKinds.ENTRY_MODIFY);
                final ExecutorService ex = Executors.newSingleThreadExecutor();
                ex.execute(() -> {
                    while (running) {
                        try {
                            WatchKey event = watcher.take();

                            if (!event.pollEvents().isEmpty()) {
                                logger.info("Repository has changed, will dynamically reload the model...");
                                GitRepositoryModel repoModel = GitRepositoryParser.buildModel(repoDir);
                                logger.info("Model reload complete...");
                                model = repoModel;
                            }

                            event.reset();

                        } catch (InterruptedException e) {
                            logger.error("Error while polling filesystem.", ex);
                        } catch (ClosedWatchServiceException err) {
                            logger.error("Watch service stopped.");
                            break;
                        }
                    }
                });

                logger.debug("Registering shutdown hook to release resources... " +
                        "this can probably be better solved by spring.");
                Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                    try {
                        watcher.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    key.cancel();
                    ex.shutdownNow();
                }));

            } catch (IOException ex) {
                logger.error("Error while attempting to register filesystem watcher.");
            }

        } catch (Exception ex) {
            logger.error("Error while connecting to repository. ", ex);
        }

    }

    @PreDestroy
    public void shutdown() {
        logger.debug("Shutting down... will try to clean up.");
        running = false;
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
