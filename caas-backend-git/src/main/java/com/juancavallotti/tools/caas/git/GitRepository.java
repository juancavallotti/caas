package com.juancavallotti.tools.caas.git;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.errors.RepositoryNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 * Assists
 */
public class GitRepository {

    private static final Logger logger = LoggerFactory.getLogger(GitRepository.class);

    private final String remotePath;
    private final String localPath;
    private final String branch;

    public GitRepository(String remotePath, String localPath, String branch) {
        this.remotePath = remotePath;
        this.localPath = localPath;
        this.branch = branch;
    }

    public Git buildGit() {
        File local = new File(localPath);

        try {
            logger.debug("Attempting to open repository at: {}", localPath);
            return Git.open(local);
        } catch (RepositoryNotFoundException ex) {
            //we try to clone
            return cloneGitRepo();
        } catch (IOException ex) {
            logger.error("Got exception while opening local repository.", ex);
        }

        return null;
    }

    private Git cloneGitRepo() {
        try {
            logger.debug("Attempting to clone from: {}", remotePath);
            return Git.cloneRepository()
                    .setURI(remotePath)
                    .setDirectory(new File(localPath))
                    .setCloneAllBranches(true)
                    .call();

        } catch (Exception ex) {
            logger.error("Got exception while cloning into repository...");
        }
        return null;
    }

}
