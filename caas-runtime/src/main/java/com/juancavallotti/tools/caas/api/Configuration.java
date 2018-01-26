package com.juancavallotti.tools.caas.api;

import com.juancavallotti.tools.caas.api.DefaultConfigurationElement;
import com.juancavallotti.tools.caas.jaxrs.ext.PATCH;

import java.io.InputStream;
import java.lang.Object;
import java.lang.String;
import java.util.List;
import javax.ws.rs.*;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;

@Path("/configuration")
public interface Configuration {
  @GET
  @Produces("application/json")
  @Consumes
  GetConfigurationResponse getConfiguration();

  @POST
  @Consumes("application/json")
  PostConfigurationResponse postConfiguration(DefaultConfigurationElement entity);

  @POST
  @Path("/{application}/{configVersion}/copy/{toVersion}")
  @Produces("application/json")
  @Consumes
  PostConfigurationCopyResponse postConfigurationCopy();

  @GET
  @Path("/{application}/{configVersion}/{env}")
  @Produces({
      "application/json",
      "text/plain"
  })
  @Consumes
  GetAppConfigurationResponse getApplicationConfiguration(
          @PathParam("application") String application,
          @PathParam("configVersion") String version,
          @PathParam("env") String environment);

  @PUT
  @Path("/{application}/{configVersion}/{env}")
  @Consumes("application/json")
  PutAppConfigurationResponse putConfiguration(
          @PathParam("application") String application,
          @PathParam("configVersion") String version,
          @PathParam("env") String environment,
          DefaultConfigurationElement entity);

  @PATCH
  @Path("/{application}/{configVersion}/{env}")
  @Consumes("application/json")
  PatchAppConfigurationResponse patchConfiguration(
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
  @Consumes
  GetConfigurationDynamicResponse getConfigurationDynamic(
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
  PutConfigurationDynamicResponse putConfigurationDynamic(
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
  PostConfigurationPromoteResponse postConfigurationPromote();

  class GetConfigurationResponse extends ResponseDelegate {
    private GetConfigurationResponse(Response response, Object entity) {
      super(response, entity);
    }

    private GetConfigurationResponse(Response response) {
      super(response);
    }

    public static GetConfigurationResponse respond200WithApplicationJson(List<ConfigCoordinate> entity) {
      Response.ResponseBuilder responseBuilder = status(200).header("Content-Type", "application/json");
      GenericEntity<List<ConfigCoordinate>> wrappedEntity = new GenericEntity<List<ConfigCoordinate>>(entity){};
      responseBuilder.entity(wrappedEntity);
      return new GetConfigurationResponse(responseBuilder.build(), wrappedEntity);
    }

    public static GetConfigurationResponse respond400WithApplicationJson(Object entity) {
      Response.ResponseBuilder responseBuilder = status(400).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetConfigurationResponse(responseBuilder.build(), entity);
    }

    public static GetConfigurationResponse respond401() {
      Response.ResponseBuilder responseBuilder = status(401);
      return new GetConfigurationResponse(responseBuilder.build());
    }

    public static GetConfigurationResponse respond403() {
      Response.ResponseBuilder responseBuilder = status(403);
      return new GetConfigurationResponse(responseBuilder.build());
    }

    public static GetConfigurationResponse respond500() {
      Response.ResponseBuilder responseBuilder = status(500);
      return new GetConfigurationResponse(responseBuilder.build());
    }
  }

  class PostConfigurationResponse extends ResponseDelegate {
    private PostConfigurationResponse(Response response, Object entity) {
      super(response, entity);
    }

    private PostConfigurationResponse(Response response) {
      super(response);
    }

    public static PostConfigurationResponse respond400WithApplicationJson(Object entity) {
      Response.ResponseBuilder responseBuilder = status(400).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PostConfigurationResponse(responseBuilder.build(), entity);
    }

    public static PostConfigurationResponse respond401() {
      Response.ResponseBuilder responseBuilder = status(401);
      return new PostConfigurationResponse(responseBuilder.build());
    }

    public static PostConfigurationResponse respond403() {
      Response.ResponseBuilder responseBuilder = status(403);
      return new PostConfigurationResponse(responseBuilder.build());
    }

    public static PostConfigurationResponse respond500() {
      Response.ResponseBuilder responseBuilder = status(500);
      return new PostConfigurationResponse(responseBuilder.build());
    }

    public static PostConfigurationResponse respond202() {
      Response.ResponseBuilder responseBuilder = status(202);
      return new PostConfigurationResponse(responseBuilder.build());
    }
  }

  class PostConfigurationCopyResponse extends ResponseDelegate {
    private PostConfigurationCopyResponse(Response response, Object entity) {
      super(response, entity);
    }

    private PostConfigurationCopyResponse(Response response) {
      super(response);
    }

    public static PostConfigurationCopyResponse respond400WithApplicationJson(Object entity) {
      Response.ResponseBuilder responseBuilder = status(400).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PostConfigurationCopyResponse(responseBuilder.build(), entity);
    }

    public static PostConfigurationCopyResponse respond401() {
      Response.ResponseBuilder responseBuilder = status(401);
      return new PostConfigurationCopyResponse(responseBuilder.build());
    }

    public static PostConfigurationCopyResponse respond403() {
      Response.ResponseBuilder responseBuilder = status(403);
      return new PostConfigurationCopyResponse(responseBuilder.build());
    }

    public static PostConfigurationCopyResponse respond500() {
      Response.ResponseBuilder responseBuilder = status(500);
      return new PostConfigurationCopyResponse(responseBuilder.build());
    }

    public static PostConfigurationCopyResponse respond202() {
      Response.ResponseBuilder responseBuilder = status(202);
      return new PostConfigurationCopyResponse(responseBuilder.build());
    }
  }

  class GetAppConfigurationResponse extends ResponseDelegate {
    private GetAppConfigurationResponse(Response response, Object entity) {
      super(response, entity);
    }

    private GetAppConfigurationResponse(Response response) {
      super(response);
    }

    public static GetAppConfigurationResponse respond200WithApplicationJson(ConfigurationElement entity) {
      Response.ResponseBuilder responseBuilder = status(200).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetAppConfigurationResponse(responseBuilder.build(), entity);
    }

    public static GetConfigurationResponse respond400WithApplicationJson(Object entity) {
      Response.ResponseBuilder responseBuilder = status(400).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetConfigurationResponse(responseBuilder.build(), entity);
    }

    public static GetConfigurationResponse respond401() {
      Response.ResponseBuilder responseBuilder = status(401);
      return new GetConfigurationResponse(responseBuilder.build());
    }

    public static GetConfigurationResponse respond403() {
      Response.ResponseBuilder responseBuilder = status(403);
      return new GetConfigurationResponse(responseBuilder.build());
    }

    public static GetConfigurationResponse respond500() {
      Response.ResponseBuilder responseBuilder = status(500);
      return new GetConfigurationResponse(responseBuilder.build());
    }

    public static GetAppConfigurationResponse respond404WithTextPlain(Object entity) {
      Response.ResponseBuilder responseBuilder = status(404).header("Content-Type", "text/plain");
      responseBuilder.entity(entity);
      return new GetAppConfigurationResponse(responseBuilder.build(), entity);
    }
  }

  class PutAppConfigurationResponse extends ResponseDelegate {
    private PutAppConfigurationResponse(Response response, Object entity) {
      super(response, entity);
    }

    private PutAppConfigurationResponse(Response response) {
      super(response);
    }

    public static PutAppConfigurationResponse respond202() {
      Response.ResponseBuilder responseBuilder = status(202);
      return new PutAppConfigurationResponse(responseBuilder.build());
    }

    public static PutAppConfigurationResponse respond400WithApplicationJson(Object entity) {
      Response.ResponseBuilder responseBuilder = status(400).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PutAppConfigurationResponse(responseBuilder.build(), entity);
    }

    public static PutAppConfigurationResponse respond401() {
      Response.ResponseBuilder responseBuilder = status(401);
      return new PutAppConfigurationResponse(responseBuilder.build());
    }

    public static PutAppConfigurationResponse respond403() {
      Response.ResponseBuilder responseBuilder = status(403);
      return new PutAppConfigurationResponse(responseBuilder.build());
    }

    public static PutAppConfigurationResponse respond500() {
      Response.ResponseBuilder responseBuilder = status(500);
      return new PutAppConfigurationResponse(responseBuilder.build());
    }

    public static PutAppConfigurationResponse respond404WithTextPlain(Object entity) {
      Response.ResponseBuilder responseBuilder = status(404).header("Content-Type", "text/plain");
      responseBuilder.entity(entity);
      return new PutAppConfigurationResponse(responseBuilder.build(), entity);
    }
  }

  class PatchAppConfigurationResponse extends ResponseDelegate {
    private PatchAppConfigurationResponse(Response response, Object entity) {
      super(response, entity);
    }

    private PatchAppConfigurationResponse(Response response) {
      super(response);
    }

    public static PatchAppConfigurationResponse respond202() {
      Response.ResponseBuilder responseBuilder = status(202);
      return new PatchAppConfigurationResponse(responseBuilder.build());
    }

    public static PatchAppConfigurationResponse respond400WithApplicationJson(Object entity) {
      Response.ResponseBuilder responseBuilder = status(400).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PatchAppConfigurationResponse(responseBuilder.build(), entity);
    }

    public static PatchAppConfigurationResponse respond401() {
      Response.ResponseBuilder responseBuilder = status(401);
      return new PatchAppConfigurationResponse(responseBuilder.build());
    }

    public static PatchAppConfigurationResponse respond403() {
      Response.ResponseBuilder responseBuilder = status(403);
      return new PatchAppConfigurationResponse(responseBuilder.build());
    }

    public static PatchAppConfigurationResponse respond500() {
      Response.ResponseBuilder responseBuilder = status(500);
      return new PatchAppConfigurationResponse(responseBuilder.build());
    }

    public static PatchAppConfigurationResponse respond404WithTextPlain(Object entity) {
      Response.ResponseBuilder responseBuilder = status(404).header("Content-Type", "text/plain");
      responseBuilder.entity(entity);
      return new PatchAppConfigurationResponse(responseBuilder.build(), entity);
    }
  }

  class GetConfigurationDynamicResponse extends ResponseDelegate {
    private GetConfigurationDynamicResponse(Response response, Object entity) {
      super(response, entity);
    }

    private GetConfigurationDynamicResponse(Response response) {
      super(response);
    }

    public static HeadersFor200 headersFor200() {
      return new HeadersFor200();
    }

    public static GetConfigurationDynamicResponse respond200WithContentType(Object entity, String contentType) {
      Response.ResponseBuilder responseBuilder = status(200).header("Content-Type", contentType);
      responseBuilder.entity(entity);
      HeadersFor200 headers = headersFor200();
      headers.toResponseBuilder(responseBuilder);
      return new GetConfigurationDynamicResponse(responseBuilder.build(), entity);
    }


    public static GetConfigurationDynamicResponse respond400WithApplicationJson(Object entity) {
      Response.ResponseBuilder responseBuilder = status(400).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetConfigurationDynamicResponse(responseBuilder.build(), entity);
    }

    public static GetConfigurationDynamicResponse respond401() {
      Response.ResponseBuilder responseBuilder = status(401);
      return new GetConfigurationDynamicResponse(responseBuilder.build());
    }

    public static GetConfigurationDynamicResponse respond403() {
      Response.ResponseBuilder responseBuilder = status(403);
      return new GetConfigurationDynamicResponse(responseBuilder.build());
    }

    public static GetConfigurationDynamicResponse respond500() {
      Response.ResponseBuilder responseBuilder = status(500);
      return new GetConfigurationDynamicResponse(responseBuilder.build());
    }

    public static GetConfigurationDynamicResponse respond404WithTextPlain(Object entity) {
      Response.ResponseBuilder responseBuilder = status(404).header("Content-Type", "text/plain");
      responseBuilder.entity(entity);
      return new GetConfigurationDynamicResponse(responseBuilder.build(), entity);
    }

    public static class HeadersFor200 extends HeaderBuilderBase {
      private HeadersFor200() {
      }

      public HeadersFor200 withContentType(final String p) {
        headerMap.put("Content-Type", p.toString());
        return this;
      }
    }
  }

  class PutConfigurationDynamicResponse extends ResponseDelegate {
    private PutConfigurationDynamicResponse(Response response, Object entity) {
      super(response, entity);
    }

    private PutConfigurationDynamicResponse(Response response) {
      super(response);
    }

    public static PutConfigurationDynamicResponse respond202() {
      Response.ResponseBuilder responseBuilder = status(202);
      return new PutConfigurationDynamicResponse(responseBuilder.build());
    }

    public static PutConfigurationDynamicResponse respond400WithApplicationJson(Object entity) {
      Response.ResponseBuilder responseBuilder = status(400).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PutConfigurationDynamicResponse(responseBuilder.build(), entity);
    }

    public static PutConfigurationDynamicResponse respond401() {
      Response.ResponseBuilder responseBuilder = status(401);
      return new PutConfigurationDynamicResponse(responseBuilder.build());
    }

    public static PutConfigurationDynamicResponse respond403() {
      Response.ResponseBuilder responseBuilder = status(403);
      return new PutConfigurationDynamicResponse(responseBuilder.build());
    }

    public static PutConfigurationDynamicResponse respond500() {
      Response.ResponseBuilder responseBuilder = status(500);
      return new PutConfigurationDynamicResponse(responseBuilder.build());
    }

    public static PutConfigurationDynamicResponse respond404WithTextPlain(Object entity) {
      Response.ResponseBuilder responseBuilder = status(404).header("Content-Type", "text/plain");
      responseBuilder.entity(entity);
      return new PutConfigurationDynamicResponse(responseBuilder.build(), entity);
    }
  }

  class PostConfigurationPromoteResponse extends ResponseDelegate {
    private PostConfigurationPromoteResponse(Response response, Object entity) {
      super(response, entity);
    }

    private PostConfigurationPromoteResponse(Response response) {
      super(response);
    }

    public static PostConfigurationPromoteResponse respond400WithApplicationJson(Object entity) {
      Response.ResponseBuilder responseBuilder = status(400).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PostConfigurationPromoteResponse(responseBuilder.build(), entity);
    }

    public static PostConfigurationPromoteResponse respond401() {
      Response.ResponseBuilder responseBuilder = status(401);
      return new PostConfigurationPromoteResponse(responseBuilder.build());
    }

    public static PostConfigurationPromoteResponse respond403() {
      Response.ResponseBuilder responseBuilder = status(403);
      return new PostConfigurationPromoteResponse(responseBuilder.build());
    }

    public static PostConfigurationPromoteResponse respond500() {
      Response.ResponseBuilder responseBuilder = status(500);
      return new PostConfigurationPromoteResponse(responseBuilder.build());
    }

    public static PostConfigurationPromoteResponse respond202() {
      Response.ResponseBuilder responseBuilder = status(202);
      return new PostConfigurationPromoteResponse(responseBuilder.build());
    }
  }
}
