package com.juancavallotti.tools;

import com.juancavallotti.tools.caas.spi.ConfigurationServiceBackend;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.serviceloader.ServiceFactoryBean;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.embedded.EmbeddedServletContainerInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

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


        //TODO - Be able to load backends based on configuration.

        return sl.findFirst().get();
    }

}
