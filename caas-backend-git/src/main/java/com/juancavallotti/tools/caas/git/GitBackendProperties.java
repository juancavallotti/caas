package com.juancavallotti.tools.caas.git;

import com.juancavallotti.tools.caas.spi.BackendProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(GitBackendProperties.PROPERTIES_PREFIX)
public class GitBackendProperties implements BackendProperties {

    static final String PROPERTIES_PREFIX = "gitbackend";

    private String repoUrl;

    private String branch = "master";

    private String localPath;

    public String getRepoUrl() {
        return repoUrl;
    }

    public void setRepoUrl(String repoUrl) {
        this.repoUrl = repoUrl;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    @Override
    public String getPropertiesPrefix() {
        return PROPERTIES_PREFIX;
    }
}
