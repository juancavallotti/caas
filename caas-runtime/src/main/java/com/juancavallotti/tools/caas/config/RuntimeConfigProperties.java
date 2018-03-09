package com.juancavallotti.tools.caas.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("runtime")
public class RuntimeConfigProperties {

    private String backend;


    public String getBackend() {
        return backend;
    }

    public void setBackend(String backend) {
        this.backend = backend;
    }
}
