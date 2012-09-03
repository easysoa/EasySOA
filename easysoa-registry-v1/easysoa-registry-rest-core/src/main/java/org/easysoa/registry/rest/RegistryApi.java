package org.easysoa.registry.rest;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.easysoa.registry.rest.marshalling.OperationResult;
import org.easysoa.registry.rest.marshalling.SoaNodeInformation;

@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface RegistryApi {

    @POST
    OperationResult post(SoaNodeInformation soaNodeInfo) throws Exception;

    @GET
    SoaNodeInformation get() throws Exception;

    @GET
    @Path("{doctype}")
    List<SoaNodeInformation> get(@PathParam("doctype") String doctype) throws Exception;

    @GET
    @Path("{doctype}/{name}")
    SoaNodeInformation get(@PathParam("doctype") String doctype, @PathParam("name") String name) throws Exception;

    @DELETE
    @Path("{doctype}/{name}")
    OperationResult delete(@PathParam("doctype") String doctype, @PathParam("name") String name) throws Exception;

    @DELETE
    @Path("{doctype}/{name}/{correlatedDoctype}/{correlatedName}")
    OperationResult delete(@PathParam("doctype") String doctype, @PathParam("name") String name,
            @PathParam("correlatedDoctype") String correlatedDoctype,
            @PathParam("correlatedName") String correlatedName) throws Exception;

}