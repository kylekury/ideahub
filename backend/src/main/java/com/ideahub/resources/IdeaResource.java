package com.ideahub.resources;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import com.ideahub.dao.IdeaDAO;
import com.ideahub.dao.IdeaPartTypeDAO;
import com.ideahub.model.IdeaPartType;
import com.ideahub.model.User;

import io.dropwizard.auth.Auth;
import io.dropwizard.hibernate.UnitOfWork;
import jodd.petite.meta.PetiteBean;
import lombok.AllArgsConstructor;

@Path("/idea")
@PetiteBean
@AllArgsConstructor
public class IdeaResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(IdeaResource.class);
    
    private final IdeaDAO ideaDAO;
    private final IdeaPartTypeDAO ideaPartTypeDAO;
    
    @GET
    @Path("/definition")
    @Timed
    @ExceptionMetered
    @Consumes
    @UnitOfWork
    public List<IdeaPartType> getIdeaDefinition(@Context final UriInfo uriInfo, @Auth final User authenticatedUser) throws Exception {
        return ideaPartTypeDAO.getIdeaDefinition();
    }
}