package com.juancavallotti.tools.caas.api;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.util.Map;

public class ConfigurationServiceResponse extends ResponseDelegate {

    private static final String CONTENT_TYPE = "Content-Type";

    private ConfigurationServiceResponse(Response response) {
        super(response);
    }


    public static ConfigurationServiceResponse respond500() {
        return wrap(status(500));
    }

    public static ConfigurationServiceResponse respond202() {
        return wrap(status(202));
    }

    private static ConfigurationServiceResponse wrap(ResponseBuilder builder) {
        return new ConfigurationServiceResponse(builder.build());
    }

    public static ConfigurationServiceResponse respond200WithContentType(Object data, String type) {
        return wrap(
                status(200)
                .entity(data)
                .type(type)
        );
    }

    public static ConfigurationServiceResponse respond404WithTextPlain(String body) {
        return wrap(
                status(404)
                .entity(body)
                .type(MediaType.TEXT_PLAIN)
        );
    }

    public static ConfigurationServiceResponse respond400WithApplicationJson(Object body) {
        return wrap(
                status(400)
                .entity(body)
                .type(MediaType.APPLICATION_JSON)
        );
    }

    public static ConfigurationServiceResponse respond200WithApplicationJson(Object entity) {
        return respond200WithContentType(entity, MediaType.APPLICATION_JSON);
    }

    public static ConfigurationServiceResponse respond404() {
        return wrap(
                status(404)
                .entity("Not found!")
                .type(MediaType.TEXT_PLAIN)
        );
    }
}
