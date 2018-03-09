package com.juancavallotti.tools.caas;

import com.juancavallotti.tools.caas.impl.ConfigurationServiceImpl;
import com.juancavallotti.tools.caas.impl.admin.AdminImpl;
import org.glassfish.jersey.logging.LoggingFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.stereotype.Component;

import javax.ws.rs.ApplicationPath;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
@ApplicationPath("api")
public class CaasResourceConfig extends ResourceConfig {

    public CaasResourceConfig() {
        register(new LoggingFeature(Logger.getLogger(LoggingFeature.DEFAULT_LOGGER_NAME), Level.ALL, LoggingFeature.Verbosity.PAYLOAD_TEXT, Integer.MAX_VALUE));
        register(ConfigurationServiceImpl.class);
        register(AdminImpl.class);
    }

}
