package com.juancavallotti.tools.caas;

import com.juancavallotti.tools.caas.config.RuntimeConfigProperties;
import com.juancavallotti.tools.caas.spi.ConfigurationServiceBackend;
import com.juancavallotti.tools.caas.spi.ConfigurationServiceDataProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.context.embedded.EmbeddedServletContainerInitializedEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.*;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.StringUtils;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CommonsRequestLoggingFilter;
import org.springframework.web.filter.CorsFilter;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.stream.Collectors;


import static com.juancavallotti.tools.caas.RuntimeProperties.*;

@Configuration
@EnableAutoConfiguration
@ComponentScan(basePackages = "com.juancavallotti.tools")
@EnableConfigurationProperties(RuntimeConfigProperties.class)
@PropertySource(value = "${"+ CONFIG_LOCATION + ":" + SpringBeansConfig.DEFAULT_CONFIG_LOCATION + " }", ignoreResourceNotFound = false)
public class SpringBeansConfig implements ApplicationListener<EmbeddedServletContainerInitializedEvent> {

    public static final String DEFAULT_CONFIG_LOCATION = "classpath://defaults.properties";

    private static final Logger logger = LoggerFactory.getLogger(SpringBeansConfig.class);

    @Value("${" + CONFIG_LOCATION + ":"+ DEFAULT_CONFIG_LOCATION +"}")
    private String configLocation;

    @Inject
    private RuntimeConfigProperties runtimeConfig;

    @Inject
    private ServerProperties serverConfig;

    @Inject
    private ApplicationContext appContext;

    @Override
    public void onApplicationEvent(EmbeddedServletContainerInitializedEvent event) {
        logger.info("CaaS Service is Running");

        //various parameters of the server.
        String host = serverConfig.getAddress() == null ? "localhost" : serverConfig.getAddress().getHostName();
        String port = Integer.toString(event.getEmbeddedServletContainer().getPort());
        String path = serverConfig.getContextPath();
        String protocol = serverConfig.getSsl() != null && serverConfig.getSsl().isEnabled() ? "https" : "http";

        if (path == null) {
            path = "";
        }

        logger.info("You may access CaaS API Console by " +
                "accessing the following url ----> " +
                "{}://{}:{}{}", protocol, host, port, path);
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
    public FilterRegistrationBean configureRequestLoggingFilter() {

        CommonsRequestLoggingFilter ret = new CommonsRequestLoggingFilter();

        ret.setIncludeClientInfo(true);
        ret.setIncludeHeaders(true);
        ret.setIncludePayload(true);
        ret.setIncludeQueryString(true);

        FilterRegistrationBean bean = new FilterRegistrationBean(ret);
        bean.setOrder(0);

        return bean;
    }

    @Bean
    public ConfigurationServiceBackend findBackend() {

        ServiceLoader<ConfigurationServiceBackend> sl = ServiceLoader.load(ConfigurationServiceBackend.class);

        if (StringUtils.isEmpty(runtimeConfig.getBackend())) {

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
                .filter(provider -> runtimeConfig.getBackend().equals(provider.type().getName()))
                .findFirst().orElse(null);

        if (ret != null) {
            return ret.get();
        }

        logger.error("Backend {} not found.", runtimeConfig.getBackend());
        logger.error("Available backends are:");

        sl.stream().forEach(backend -> logger.error("\t{}", backend.type().getName()));

        throw new RuntimeException("No suitable backend found.");

    }

    @Bean
    public List<ConfigurationServiceDataProcessor> findPreProcessors() {

        ServiceLoader<ConfigurationServiceDataProcessor> sl = ServiceLoader.load(ConfigurationServiceDataProcessor.class);

        List<String> enabledProcessors = runtimeConfig.getEnabledDataPreProcessors();

        return sl.stream()
                .filter(dpp -> {
                    if (enabledProcessors.contains(dpp.type().getName())) {
                        return true;
                    }
                    //maybe is enabled by default.
                    return dpp.get().enabledByDefault();
                }).map(dpp -> {

                    //since we're iterating over the filtered collection, why not use the effort in registeting the
                    //individual beans too right?
                    ConfigurationServiceDataProcessor p = dpp.get();
                    String className = dpp.type().getName();
                    appContext.getAutowireCapableBeanFactory().autowireBean(p);
                    appContext.getAutowireCapableBeanFactory().initializeBean(p, className);
                    return p;
                })
                .collect(Collectors.toList());
    }
}
