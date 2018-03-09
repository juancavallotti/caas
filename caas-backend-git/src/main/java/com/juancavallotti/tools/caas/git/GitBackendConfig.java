package com.juancavallotti.tools.caas.git;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(GitBackendProperties.class)
public class GitBackendConfig {
}
