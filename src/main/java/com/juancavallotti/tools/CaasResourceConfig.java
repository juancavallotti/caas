package com.juancavallotti.tools;

import com.juancavallotti.tools.caas.impl.ConfigurationServiceImpl;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.stereotype.Component;

import javax.ws.rs.ApplicationPath;

@Component
@ApplicationPath("api")
public class CaasResourceConfig extends ResourceConfig {

    public CaasResourceConfig() {
        register(ConfigurationServiceImpl.class);
    }

}
