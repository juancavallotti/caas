package com.juancavallotti.tools.caas.api.admin;

import com.juancavallotti.tools.caas.api.ConfigurationServiceResponse;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/admin")
public interface Admin {

    @GET
    @Path("/serverInfo")
    public ConfigurationServiceResponse getServerInfo();

}
