package com.juancavallotti.tools.caas.git;

import com.juancavallotti.tools.caas.api.*;
import com.juancavallotti.tools.caas.git.model.GitConfigCoordinate;
import com.juancavallotti.tools.caas.git.model.GitRepositoryModel;
import com.juancavallotti.tools.caas.spi.ConfigurationServiceBackend;
import com.juancavallotti.tools.caas.spi.ConfigurationServiceBackendException;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.util.*;

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

    @PostConstruct
    public void init() {
        logger.info("Loaded Git Configuration Service, backend url: {}, local repo path: {}", repoBackend, localPath);
        try {
            repoDir = new File(localPath);
//            FileRepositoryBuilder builder = new FileRepositoryBuilder();
//            Repository repo = builder.setGitDir(new File(repoBackend + File.separator + ".git"))
//                    .setMustExist(true)
//                    .findGitDir()
//                    .readEnvironment()
//                    .build();
//            repo.resolve("HEAD");
//            git = new Git(repo);

            logger.debug("Cloning or opening git repository...");
            git = new GitRepository(repoBackend, localPath, branch).buildGit();

            if (git == null) {
                logger.error("Error while connecting to GIT repository. Check settings.");
                return;
            }

            logger.debug("Checking out specific branch {}", branch);
            git.checkout().setName(branch);

            logger.debug("Parsing directory...");
            //build the model.
            model = GitRepositoryParser.buildModel(repoDir);

        } catch (Exception ex) {
            logger.error("Error while connecting to repository. ", ex);
        }

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
