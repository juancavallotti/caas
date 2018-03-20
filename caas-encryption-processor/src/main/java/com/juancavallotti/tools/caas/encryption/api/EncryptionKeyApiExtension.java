package com.juancavallotti.tools.caas.encryption.api;

import com.juancavallotti.tools.caas.encryption.EncryptionProperties;
import com.juancavallotti.tools.caas.encryption.KeyBuilder;
import com.juancavallotti.tools.caas.spi.ConfigurationServiceApiExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Map;

@Path("/admin/security")
public class EncryptionKeyApiExtension implements ConfigurationServiceApiExtension {

    private static final Logger logger = LoggerFactory.getLogger(EncryptionKeyApiExtension.class);

    @Autowired
    private EncryptionProperties config;

    @GET
    @Path("/wrappedKey")
    public Response wrappedKey() {

        if (!config.isExtensionApiEnabled()) {
            return Response
                    .status(404)
                    .type(MediaType.APPLICATION_JSON_TYPE)
                    .entity(Map.of("message", "Encryption Extension API is not Enabled"))
                    .build();
        }

        WrappedKey entity = KeyBuilder.builder(config).wrapKey(config.getKeyAlias(), config.getKeyPassword());
        
        return Response
                .status(200)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .entity(entity)
                .build();
    }

}
