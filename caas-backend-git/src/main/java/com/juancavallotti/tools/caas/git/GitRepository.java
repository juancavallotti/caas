package com.juancavallotti.tools.caas.git;

import org.eclipse.jgit.api.CheckoutCommand;
import org.eclipse.jgit.api.CheckoutResult;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.RefNotFoundException;
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
            Git ret = Git.cloneRepository()
                    .setURI(remotePath)
                    .setDirectory(new File(localPath))
                    .setCloneAllBranches(true)
                    .call();

            //try to pull the branch that we need from origin.
            try {
                ret.fetch().setRemote("origin").call();
            } catch (RefNotFoundException ex) {
                logger.debug("Fetching remote branch failed!", ex);
            }
            return ret;
        } catch (Exception ex) {
            logger.error("Got exception while cloning into repository...", ex);
        }
        return null;
    }

    public void checkoutBranch(Git git) {
        logger.info("Checking out into branch {}", branch);

        boolean callPull = false;

        try {
            //try to retrieve the branch.
            CheckoutCommand  cmd  = git.checkout()
                    .setName(branch);

            try {
                cmd.call();
            } catch (RefNotFoundException ex) {
                //branch may currently not exist locally.
                logger.debug("Checkout failed with status: ", ex.getMessage());
                cmd.setCreateBranch(true);
                callPull = true;
                //try to call the command.
                cmd.call();
            }

            //after checking out we might want to call pull.
            if (callPull) {
                git.pull().setRemoteBranchName(branch).call();
            }

        } catch (GitAPIException ex) {
            logger.error("Error while checking out branch...", ex);
        }
    }

    public void printStatus(Git git, Logger targetLogger) {

        try {
            String branch = git.getRepository().getBranch();
            logger.info("Current branch is: {}", branch);
        } catch (Exception ex) {
            logger.error("Error while calling GIT STATUS", ex);
        }

    }

}
