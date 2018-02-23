package com.juancavallotti.tools.caas.api;

import com.juancavallotti.tools.caas.jaxrs.ext.PATCH;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.io.InputStream;
import java.lang.String;
import javax.ws.rs.*;

@CrossOrigin
@Path("/configuration")
public interface Configuration {

    @GET
    @Produces("application/json")
    @Consumes
    ConfigurationServiceResponse getConfiguration();

    @POST
    @Consumes("application/json")
    ConfigurationServiceResponse postConfiguration(DefaultConfigurationElement entity);

    @POST
    @Path("/{application}/{configVersion}/copy/{toVersion}")
    @Produces("application/json")
    @Consumes
    ConfigurationServiceResponse postConfigurationCopy(
            @PathParam("application") String application,
            @PathParam("configVersion") String version,
            @PathParam("toVersion") String targetVersion
    );

    @GET
    @Path("/{application}/{configVersion}/{env}")
    @Produces({
            "application/json",
            "text/plain"
    })
    @Consumes
    ConfigurationServiceResponse getApplicationConfiguration(
            @PathParam("application") String application,
            @PathParam("configVersion") String version,
            @PathParam("env") String environment);

    @PUT
    @Path("/{application}/{configVersion}/{env}")
    @Consumes("application/json")
    ConfigurationServiceResponse putConfiguration(
            @PathParam("application") String application,
            @PathParam("configVersion") String version,
            @PathParam("env") String environment,
            DefaultConfigurationElement entity);

    @PATCH
    @Path("/{application}/{configVersion}/{env}")
    @Consumes("application/json")
    ConfigurationServiceResponse patchConfiguration(
            @PathParam("application") String application,
            @PathParam("configVersion") String version,
            @PathParam("env") String environment,
            DefaultConfigurationElement entity);

    @GET
    @Path("/{application}/{configVersion}/{env}/dynamic/{key}")
    @Produces({
            "application/xml",
            "application/csv",
            "application/json",
            "application/weave",
            "application/yaml",
            "text/plain"
    })
    ConfigurationServiceResponse getConfigurationDynamic(
            @PathParam("application") String application,
            @PathParam("configVersion") String version,
            @PathParam("env") String environment,
            @PathParam("key") String key
    );

    @PUT
    @Path("/{application}/{configVersion}/{env}/dynamic/{key}")
    @Consumes({
            "application/json",
            "application/xml",
            "application/weave",
            "application/yaml",
            "text/plain",
            "application/csv"
    })
    ConfigurationServiceResponse putConfigurationDynamic(
            @PathParam("application") String application,
            @PathParam("configVersion") String version,
            @PathParam("env") String environment,
            @PathParam("key") String key,
            @HeaderParam("Content-Type") String contentType,
            InputStream entity);

    @POST
    @Path("/{application}/{configVersion}/{env}/promote/{toEnv}")
    @Produces("application/json")
    @Consumes
    ConfigurationServiceResponse postConfigurationPromote(
            @PathParam("application") String application,
            @PathParam("configVersion") String version,
            @PathParam("env") String environment,
            @PathParam("toEnv") String targetEnvironment
    );

}
