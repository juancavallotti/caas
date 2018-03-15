package com.juancavallotti.tools.caas.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Collections;
import java.util.List;

@ConfigurationProperties("runtime")
public class RuntimeConfigProperties {

    private String backend;

    private List<String> enabledDataPreProcessors = Collections.emptyList();

    public String getBackend() {
        return backend;
    }

    public void setBackend(String backend) {
        this.backend = backend;
    }

    public List<String> getEnabledDataPreProcessors() {
        return enabledDataPreProcessors;
    }

    public void setEnabledDataPreProcessors(List<String> enabledDataPreProcessors) {
        this.enabledDataPreProcessors = enabledDataPreProcessors;
    }
}
