package com.juancavallotti.tools.caas;

import com.juancavallotti.tools.caas.spi.ConfigurationServiceBackend;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.embedded.EmbeddedServletContainerInitializedEvent;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.util.StringUtils;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;
import java.util.Optional;
import java.util.ServiceLoader;


import static com.juancavallotti.tools.caas.RuntimeProperties.*;

@Configuration
@EnableAutoConfiguration
@ComponentScan(basePackages = "com.juancavallotti.tools")
@PropertySource(value = "${"+ CONFIG_LOCATION + ":" + SpringBeansConfig.DEFAULT_CONFIG_LOCATION + " }", ignoreResourceNotFound = false)
public class SpringBeansConfig implements ApplicationListener<EmbeddedServletContainerInitializedEvent> {

    public static final String DEFAULT_CONFIG_LOCATION = "classpath://defaults.properties";

    private static final Logger logger = LoggerFactory.getLogger(SpringBeansConfig.class);

    @Value("${" + CONFIG_LOCATION + ":"+ DEFAULT_CONFIG_LOCATION +"}")
    private String configLocation;

    @Value("${"+ RUNTIME_BACKEND +":}")
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
    public FilterRegistrationBean configureCors() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("*");
        config.addAllowedMethod("*");
        config.addAllowedHeader("*");

        source.registerCorsConfiguration("/api/**", config);

        logger.debug("Registering CORS filter: {}", source);

        FilterRegistrationBean bean = new FilterRegistrationBean(new CorsFilter(source));
        bean.setOrder(0);

        return bean;
    }

    @Bean
    public ConfigurationServiceBackend findBackend() {

        ServiceLoader<ConfigurationServiceBackend> sl = ServiceLoader.load(ConfigurationServiceBackend.class);

        if (StringUtils.isEmpty(backend)) {

            Optional<ConfigurationServiceBackend> ret = sl.findFirst();

            if (!ret.isPresent()) {
                logger.error("There are no backend implementations in the classpath!!");
                throw new RuntimeException("No present backends in classpath!");
            }

            logger.info("Selected backend implementation: {}.", ret.get().getServiceName());
            logger.info("Suitable implementations (configued through '{}') are:", CONFIG_LOCATION);
            sl.stream().forEach(backend -> logger.info("\t{}", backend.type().getName()));

            return ret.get();
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
