package com.juancavallotti.tools.caas.git;

import com.juancavallotti.tools.caas.git.model.GitConfigCoordinate;
import com.juancavallotti.tools.caas.git.model.GitDocument;
import com.juancavallotti.tools.caas.git.model.GitRepositoryModel;
import com.juancavallotti.tools.caas.git.model.ModelConventions;
import com.juancavallotti.tools.caas.git.model.settings.AppSettings;
import com.juancavallotti.tools.caas.git.model.settings.EnvironmentSettings;
import org.eclipse.jgit.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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

        AppSettings settings = readSettings(versionPath).orElse(AppSettings.builder().defaults());
        GitRepositoryParserContext context = new GitRepositoryParserContext(appName, versionName, globals, configs, versionPath);

        //scan all properties files.
        File[] candidateEnvs = versionPath
                .listFiles((FileFilter) (pathname -> pathname.isFile() && pathname.getName().endsWith(ModelConventions.ENVIRONMENT_PROPS_EXTENSION)));

        logger.debug("Found properties Files", candidateEnvs);

        Arrays.stream(candidateEnvs).forEach(envPath -> {
            parseVersionFolderEntry(settings, context, envPath);
        });
    }


    private static void parseVersionFolderEntry(AppSettings settings, GitRepositoryParserContext context, File envPath) {
        String fileName = envPath.getName();

        String appName = context.appFolderName;
        String versionName = context.versionFolderName;
        String conventionStart = settings.buildPropertiesFileTemplate(appName, versionName);
        String conventionEnd = ModelConventions.ENVIRONMENT_PROPS_EXTENSION;

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

        //this will return the settings or the convention.
        EnvironmentSettings envSettings = settings.forEnvironment(envName);

        File documentsPath = new File(context.versionFolder.getPath() + File.separator + envSettings.getDocumentsPath());

        //finally, build the coordinate.
        GitConfigCoordinate coordinate = new GitConfigCoordinate(envSettings, envPath, documentsPath);
        coordinate.setApplication(appName);
        coordinate.setVersion(versionName);
        coordinate.setEnvironment(envName);


        //check if it belongs to the globals list.
        if (settings.isGlobal() || ModelConventions.defaultGlobalAppNames.contains(appName)) {
            logger.debug("Coordinate {} is conventionally global.", appName);
            context.globals.add(coordinate);
        }

        context.configs.add(coordinate);
    }

    private static Optional<AppSettings> readSettings(File versionPath) {

        try {
            Path configFile = Files
                    .find(versionPath.toPath(), 1, (p, attr) -> ModelConventions.SETTINGS_FILE.equals(p.getFileName().toString()))
                    .findFirst().orElse(null);

            if (configFile == null) {
                logger.debug("No specific config found for app.");
                return Optional.empty();
            }

            logger.debug("Reading configuration file for application: {}", configFile);

            return Optional.of(AppSettings.builder().fromPath(configFile).build());
        } catch (IOException ex) {
            logger.error("Error while locating app config file...", ex);
            return Optional.empty();
        }
    }

    private static class GitRepositoryParserContext {

        private final String appFolderName;
        private final String versionFolderName;
        private final List<GitConfigCoordinate> globals;
        private final List<GitConfigCoordinate> configs;
        private final File versionFolder;

        public GitRepositoryParserContext(String appFolderName, String versionFolderName, List<GitConfigCoordinate> globals, List<GitConfigCoordinate> configs, File versionFolder) {
            this.appFolderName = appFolderName;
            this.versionFolderName = versionFolderName;
            this.globals = globals;
            this.configs = configs;
            this.versionFolder = versionFolder;
        }
    }

}
