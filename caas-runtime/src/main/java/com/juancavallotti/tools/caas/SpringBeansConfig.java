package com.juancavallotti.tools.caas;

import com.juancavallotti.tools.caas.spi.ConfigurationServiceBackend;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.embedded.EmbeddedServletContainerInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.util.StringUtils;

import java.util.ServiceLoader;


@Configuration
@EnableAutoConfiguration
@ComponentScan(basePackages = "com.juancavallotti.tools")
@PropertySource(value = "${config.location:" + SpringBeansConfig.DEFAULT_CONFIG_LOCATION + " }", ignoreResourceNotFound = false)
public class SpringBeansConfig implements ApplicationListener<EmbeddedServletContainerInitializedEvent> {

    public static final String DEFAULT_CONFIG_LOCATION = "classpath://defaults.properties";

    private static final Logger logger = LoggerFactory.getLogger(SpringBeansConfig.class);

    @Value("${config.location:"+ DEFAULT_CONFIG_LOCATION +"}")
    private String configLocation;

    @Value("${runtime.backend:}")
    private String backend;

    @Override
    public void onApplicationEvent(EmbeddedServletContainerInitializedEvent event) {
        logger.info("CaaS Service is Running");
        logger.info("You may access CaaS API Console by " +
                "accessing the following url ----> " +
                "http://localhost:" + event.getEmbeddedServletContainer().getPort() + "/");
        logger.info("Loaded configuration from: " + configLocation);
    }

    @Bean
    public ConfigurationServiceBackend findBackend() {

        ServiceLoader<ConfigurationServiceBackend> sl = ServiceLoader.load(ConfigurationServiceBackend.class);

        if (StringUtils.isEmpty(backend)) {
            return sl.findFirst().get();
        }

        //get named backend.
        ServiceLoader.Provider<ConfigurationServiceBackend> ret = sl.stream()
                .filter(provider -> backend.equals(provider.type().getName()))
                .findFirst().orElse(null);

        if (ret != null) {
            return ret.get();
        }

        logger.error("Backend {} not found.", backend);
        logger.error("Available backends are:");

        sl.stream().forEach(backend -> logger.error("\t{}", backend.type().getName()));

        throw new RuntimeException("No suitable backend found.");

    }

}
