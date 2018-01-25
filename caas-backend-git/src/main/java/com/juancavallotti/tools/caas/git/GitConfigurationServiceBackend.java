package com.juancavallotti.tools.caas.git;

import com.juancavallotti.tools.caas.api.*;
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

    @Value("${gitbackend.repourl}")
    private String repoBackend;

    @Value("${gitbackend.defaultenv:default}")
    private String defaultEnvironmentName;

    private Git git;
    private File repoDir;

    public GitConfigurationServiceBackend() {
        logger.info(serviceName("Git"));
    }

    @PostConstruct
    public void init() {
        logger.info("Loaded Git Configuration Service, backend url: " + repoBackend);
        try {
            repoDir = new File(repoBackend);
            FileRepositoryBuilder builder = new FileRepositoryBuilder();
            Repository repo = builder.setGitDir(new File(repoBackend + File.separator + ".git"))
                    .setMustExist(true)
                    .findGitDir()
                    .readEnvironment()
                    .build();
            repo.resolve("HEAD");
            git = new Git(repo);
        } catch (Exception ex) {
            logger.error("Error while connecting to repository. ", ex);
        }

    }


    @Override
    public List<ConfigCoordinate> listConfigurations() {

        List<ConfigCoordinate> ret = new LinkedList<>();
        Set<String> versions = new TreeSet<>();

        populateWithBranches(versions);
        populateWithTags(versions);

        //list all files
        versions.forEach((String ref) -> {
            Set<String> files = new TreeSet<>();
            populateWithFiles(files, ref);
            final String version = ref;
            files.forEach((String fileName) -> {

                //we already know the version.
                DefaultConfigCoordinate cc = new DefaultConfigCoordinate();
                cc.setVersion(version);
                parseCoordinateDetails(fileName, cc);
                ret.add(cc);
            });
        });

        return ret;
    }

    @Override
    public ConfigCoordinate createNewConfiguration(ConfigurationElement element) throws ConfigurationServiceBackendException {
        return null;
    }

    @Override
    public Document setDocument(ConfigCoordinate coordinate, String documentName, String contentType, InputStream documentData) throws ConfigurationServiceBackendException {
        return null;
    }

    @Override
    public DocumentData getDocumentData(ConfigCoordinate coordinate, String documentName) throws ConfigurationServiceBackendException {
        return null;
    }

    @Override
    public ConfigurationElement findConfiguration(String application, String environment, String version) throws ConfigurationServiceBackendException {
        return null;
    }

    private void populateWithFiles(Collection<String> container, String ref) {
        try {
            Ref r = git.checkout().setName(ref).call();

            File[] files = new File(repoBackend).listFiles((FilenameFilter)(File dir, String name) -> {
                return isConfigFile(name);
            });

            for(File f: files) {
                container.add(f.getName());
            }

        } catch (Exception ex) {
            logger.error("Could not retrieve files for ref: " + ref, ex);
        }
    }

    private void populateWithBranches(Collection<String> container) {
        try {
            List<Ref> refs = git.branchList().call();
            populateWithRefList(container, refs, "refs/branches/", "refs/heads/");
        } catch (Exception ex) {
            logger.error("Could not retrieve branches", ex);
        }
    }

    private void populateWithTags(Collection<String> container) {
        try {
            List<Ref> refs = git.tagList().call();
            populateWithRefList(container, refs, "refs/tags/");
        } catch (Exception ex) {
            logger.error("Could not retrieve tags", ex);
        }
    }

    private void populateWithRefList(Collection<String> container, List<Ref> values, String... prefix) throws Exception {

        if (values == null) {
            return;
        }

        values.stream().forEach((Ref r) -> {

            String name = r.getName();

            for(String p : prefix) {
                if (name.startsWith(p)) {
                    name = StringUtils.delete(name, p);
                    break;
                }
            }

            container.add(name);
        });
    }

    private boolean isConfigFile(String fileName) {
        return StringUtils.endsWithIgnoreCase(fileName, ".properties");
    }


    private void parseCoordinateDetails(String fileName, ConfigCoordinate coord) {

        //file must be appName-env.properties

        //first, we remove the extension.
        String appName = StringUtils.stripFilenameExtension(fileName);

        //next proceed to split
        if (fileName.contains("-")) {
            String[] parts = StringUtils.split(appName, "-");
            coord.setApplication(parts[0]);
            coord.setEnvironment(parts[1]);
        } else {
            coord.setApplication(appName);
            coord.setEnvironment(defaultEnvironmentName);
        }

    }
}
