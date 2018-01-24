package com.juancavallotti.tools.caas.api;

import java.lang.Object;
import java.lang.String;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.Response.*;

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
  GetAppConfigurationResponse getApplicationConfiguration();

  @POST
  @Path("/{application}/{configVersion}/{env}")
  @Consumes("application/json")
  PostAppConfigurationResponse postConfiguration(Object entity);

  @PUT
  @Path("/{application}/{configVersion}/{env}")
  @Consumes("application/json")
  PutConfigurationResponse putConfiguration(Object entity);

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
  GetConfigurationDynamicResponse getConfigurationDynamic();

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
  PutConfigurationDynamicResponse putConfigurationDynamic(@HeaderParam("Content-Type") String contentType, Object entity);

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

    public static GetConfigurationResponse respond200WithApplicationJson(ConfigurationElement entity) {
      Response.ResponseBuilder responseBuilder = status(200).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetConfigurationResponse(responseBuilder.build(), entity);
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

    public static GetConfigurationResponse respond404WithTextPlain(Object entity) {
      Response.ResponseBuilder responseBuilder = status(404).header("Content-Type", "text/plain");
      responseBuilder.entity(entity);
      return new GetConfigurationResponse(responseBuilder.build(), entity);
    }
  }

  class PostAppConfigurationResponse extends ResponseDelegate {
    private PostAppConfigurationResponse(Response response, Object entity) {
      super(response, entity);
    }

    private PostAppConfigurationResponse(Response response) {
      super(response);
    }

    public static PostConfigurationResponse respond202() {
      Response.ResponseBuilder responseBuilder = status(202);
      return new PostConfigurationResponse(responseBuilder.build());
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

    public static PostConfigurationResponse respond404WithTextPlain(Object entity) {
      Response.ResponseBuilder responseBuilder = status(404).header("Content-Type", "text/plain");
      responseBuilder.entity(entity);
      return new PostConfigurationResponse(responseBuilder.build(), entity);
    }
  }

  class PutConfigurationResponse extends ResponseDelegate {
    private PutConfigurationResponse(Response response, Object entity) {
      super(response, entity);
    }

    private PutConfigurationResponse(Response response) {
      super(response);
    }

    public static PutConfigurationResponse respond202() {
      Response.ResponseBuilder responseBuilder = status(202);
      return new PutConfigurationResponse(responseBuilder.build());
    }

    public static PutConfigurationResponse respond400WithApplicationJson(Object entity) {
      Response.ResponseBuilder responseBuilder = status(400).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PutConfigurationResponse(responseBuilder.build(), entity);
    }

    public static PutConfigurationResponse respond401() {
      Response.ResponseBuilder responseBuilder = status(401);
      return new PutConfigurationResponse(responseBuilder.build());
    }

    public static PutConfigurationResponse respond403() {
      Response.ResponseBuilder responseBuilder = status(403);
      return new PutConfigurationResponse(responseBuilder.build());
    }

    public static PutConfigurationResponse respond500() {
      Response.ResponseBuilder responseBuilder = status(500);
      return new PutConfigurationResponse(responseBuilder.build());
    }

    public static PutConfigurationResponse respond404WithTextPlain(Object entity) {
      Response.ResponseBuilder responseBuilder = status(404).header("Content-Type", "text/plain");
      responseBuilder.entity(entity);
      return new PutConfigurationResponse(responseBuilder.build(), entity);
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

    public static GetConfigurationDynamicResponse respond200WithApplicationJson(Object entity, HeadersFor200 headers) {
      Response.ResponseBuilder responseBuilder = status(200).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      headers.toResponseBuilder(responseBuilder);
      return new GetConfigurationDynamicResponse(responseBuilder.build(), entity);
    }

    public static GetConfigurationDynamicResponse respond200WithApplicationXml(Object entity, HeadersFor200 headers) {
      Response.ResponseBuilder responseBuilder = status(200).header("Content-Type", "application/xml");
      responseBuilder.entity(entity);
      headers.toResponseBuilder(responseBuilder);
      return new GetConfigurationDynamicResponse(responseBuilder.build(), entity);
    }

    public static GetConfigurationDynamicResponse respond200WithApplicationWeave(Object entity, HeadersFor200 headers) {
      Response.ResponseBuilder responseBuilder = status(200).header("Content-Type", "application/weave");
      responseBuilder.entity(entity);
      headers.toResponseBuilder(responseBuilder);
      return new GetConfigurationDynamicResponse(responseBuilder.build(), entity);
    }

    public static GetConfigurationDynamicResponse respond200WithApplicationYaml(Object entity, HeadersFor200 headers) {
      Response.ResponseBuilder responseBuilder = status(200).header("Content-Type", "application/yaml");
      responseBuilder.entity(entity);
      headers.toResponseBuilder(responseBuilder);
      return new GetConfigurationDynamicResponse(responseBuilder.build(), entity);
    }

    public static GetConfigurationDynamicResponse respond200WithTextPlain(Object entity, HeadersFor200 headers) {
      Response.ResponseBuilder responseBuilder = status(200).header("Content-Type", "text/plain");
      responseBuilder.entity(entity);
      headers.toResponseBuilder(responseBuilder);
      return new GetConfigurationDynamicResponse(responseBuilder.build(), entity);
    }

    public static GetConfigurationDynamicResponse respond200WithApplicationCsv(Object entity, HeadersFor200 headers) {
      Response.ResponseBuilder responseBuilder = status(200).header("Content-Type", "application/csv");
      responseBuilder.entity(entity);
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
