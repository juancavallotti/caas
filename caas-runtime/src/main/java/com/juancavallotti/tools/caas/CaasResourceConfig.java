package com.juancavallotti.tools.caas;

import com.juancavallotti.tools.caas.impl.ConfigurationServiceImpl;
import com.juancavallotti.tools.caas.impl.admin.AdminImpl;
import com.juancavallotti.tools.caas.spi.ConfigurationServiceApiExtension;
import org.glassfish.jersey.logging.LoggingFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.ws.rs.ApplicationPath;
import java.util.ServiceLoader;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
@ApplicationPath("api")
public class CaasResourceConfig extends ResourceConfig {

    public static final org.slf4j.Logger logger = LoggerFactory.getLogger(CaasResourceConfig.class);

    public CaasResourceConfig() {
        register(new LoggingFeature(Logger.getLogger(LoggingFeature.DEFAULT_LOGGER_NAME), Level.ALL, LoggingFeature.Verbosity.PAYLOAD_TEXT, Integer.MAX_VALUE));
        register(ConfigurationServiceImpl.class);
        register(AdminImpl.class);

        //register all the extension service providers.
        ServiceLoader<ConfigurationServiceApiExtension> apiExtensions = ServiceLoader.load(ConfigurationServiceApiExtension.class);

        if (logger.isDebugEnabled()) {
            logger.debug("Found {} API extension(s)", apiExtensions.stream().count());
            apiExtensions.reload();
        }

        apiExtensions.stream().forEach(ext -> {
            logger.debug("Registering API extension: {}", ext.type().getName());
            register(ext.type());
        });

    }

}
