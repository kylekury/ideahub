package com.ideahub.resources.idea;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.google.common.base.Optional;
import com.ideahub.dao.IdeaDAO;
import com.ideahub.exceptions.UserDoesntOwnIdeaException;
import com.ideahub.model.Idea;

import io.dropwizard.hibernate.UnitOfWork;
import jodd.petite.meta.PetiteBean;
import lombok.AllArgsConstructor;

@Path("/idea")
@PetiteBean
@AllArgsConstructor
public class IdeaResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(IdeaResource.class);

    private final IdeaDAO ideaDAO;

    @GET
    @Path("/{ideaId}")
    @Timed
    @ExceptionMetered
    @Produces(MediaType.APPLICATION_JSON)
    @UnitOfWork
    public Optional<Idea> getIdea(@PathParam("ideaId") final long ideaId) throws Exception {
        // TODO: Replace this with user lookup, anonymous view is fine as well.
        // final long userId = authenticatedUser.getId();
        final long userId = 1L;
        
        Optional<Idea> idea = ideaDAO.findById(ideaId);
        
        // TODO: There needs to be an OR condition here that also checks whether they're a collaborator
        if (idea.isPresent() && idea.get().isPrivate() && idea.get().getUserId() != userId) {
            throw new UserDoesntOwnIdeaException();
        }
        
        return idea;
    }
    
    @POST
    @Timed
    @ExceptionMetered
    @Produces(MediaType.APPLICATION_JSON)
    @UnitOfWork
    public Idea createIdea(/*@Auth final User authenticatedUser*/) throws Exception {
        Idea idea = new Idea();

        // TODO: Update this to use the authenticated user
        idea.setUserId(1L);
        // idea.setUserId(authenticatedUser.getId());
        return ideaDAO.create(idea);
    }
    
    @DELETE
    @Path("/{ideaId}")
    @Timed
    @ExceptionMetered
    @Produces(MediaType.APPLICATION_JSON)
    @UnitOfWork
    public boolean deleteIdea(/*@Auth final User authenticatedUser*/ @PathParam("ideaId") final long ideaId) throws Exception {
        // TODO: Update this to use the authenticated user
        final long userId = 1L;
        //final long userId = authenticatedUser.getId();
        
        return ideaDAO.delete(userId, ideaId);
    }
}