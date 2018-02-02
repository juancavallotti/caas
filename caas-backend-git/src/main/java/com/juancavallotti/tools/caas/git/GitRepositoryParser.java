package com.juancavallotti.tools.caas.git;

import com.fasterxml.jackson.databind.annotation.JsonAppend;
import com.juancavallotti.tools.caas.api.ConfigCoordinate;
import com.juancavallotti.tools.caas.git.model.GitConfigCoordinate;
import com.juancavallotti.tools.caas.git.model.GitDocument;
import com.juancavallotti.tools.caas.git.model.GitRepositoryModel;
import com.juancavallotti.tools.caas.git.model.ModelConventions;
import org.eclipse.jgit.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class GitRepositoryParser {

    private static final Logger logger = LoggerFactory.getLogger(GitRepositoryParser.class);

    public static GitRepositoryModel buildModel(File repositoryLocation) {

        List<GitConfigCoordinate> globals = new LinkedList<>();
        List<GitConfigCoordinate> configs = new LinkedList<>();

        File[] candidates = repositoryLocation.listFiles();

        Arrays.stream(candidates)
                .forEach(path -> { processAppPath(path, globals, configs); });

        return new GitRepositoryModel(configs, globals);
    }

    static void processAppPath(File appPath, List<GitConfigCoordinate> globals, List<GitConfigCoordinate> configs) {
        //the requisite for this to be an app path is that it must be a directory.
        logger.debug("Parsing app path: {}", appPath.getName());

        if (appPath.isFile()) {
            logger.debug("Ignoring path as it is a file...");
            return;
        }

        String appName = appPath.getName();

        if (appName.equals(ModelConventions.GIT_DIR)) {
            logger.debug("Ignoring git dir ({})...", ModelConventions.GIT_DIR);
            return;
        }

        logger.debug("Found app: {}", appName);

        //go throgh versions
        File[] candidateVersions = appPath.listFiles();

        Arrays.stream(candidateVersions)
                .forEach(path -> { processVersionPath(appName, path, globals, configs); });

    }

    static void processVersionPath(String appName, File versionPath, List<GitConfigCoordinate> globals, List<GitConfigCoordinate> configs) {

        logger.debug("Parsing version path for app {}: {}", appName, versionPath.toString());

        //the version path will contain the environments files
        if (versionPath.isFile()) {
            //ignore!
            logger.debug("Ignoring version path as it is a file...");
            return;
        }

        //we can start constructing
        //the default convention is appName__<<env>>.properties.
        String versionName = versionPath.getName();

        String conventionStart = appName + "_";
        String conventionEnd = ".properties";

        File[] candidateEnvs = versionPath.listFiles((FileFilter) (pathname -> pathname.isFile() && pathname.getName().endsWith(conventionEnd)));


        Arrays.stream(candidateEnvs)
                .forEach(envPath -> {

            String fileName = envPath.getName();

            logger.debug("Attempting to parse file in app {} version {}: {}", appName, versionName, fileName);

            if (!fileName.startsWith(conventionStart) || !fileName.endsWith(conventionEnd)) {
                logger.debug("File does not match the convention");
                return;
            }
            //remove the junk
            String envName = fileName.substring(conventionStart.length(), fileName.length() - conventionEnd.length());

            if (StringUtils.isEmptyOrNull(envName)) {
                logger.debug("File does not respect the convention...");
                return;
            }

            Properties props = new Properties();
            try {
                props.load(new FileInputStream(envPath));
            } catch (IOException ex) {
                logger.error("Could not read properties file.", ex);
            }

            File[] docs = new File(versionPath.getPath() + File.separator + ModelConventions.defaultDocsFolderPrefix + envName).listFiles();

            if (docs == null) {
                docs = new File[0];
            }

            Map<String, GitDocument> configDocs = Arrays.stream(docs)
                    .filter(file -> file.isFile())
                    .map(file -> new GitDocument(file)).collect(Collectors.toMap(gf -> gf.getKey(), gf -> gf));

            //finally, build the coordinate.
            GitConfigCoordinate coordinate = new GitConfigCoordinate(ModelConventions.defaultDocsFolderPrefix, props, configDocs);
            coordinate.setApplication(appName);
            coordinate.setVersion(versionName);
            coordinate.setEnvironment(envName);

            //check if it belongs to the globals list.
            if (ModelConventions.defaultGlobalAppNames.contains(appName)) {
                logger.debug("Coordinate {} is conventionally global.", appName);
                globals.add(coordinate);
            }

            configs.add(coordinate);
        });
    }



}
