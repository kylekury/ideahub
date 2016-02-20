package com.ideahub.resources;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import com.google.common.base.Optional;
import com.ideahub.cache.IdeaDefinitionCache;
import com.ideahub.dao.IdeaDAO;
import com.ideahub.dao.IdeaPartDAO;
import com.ideahub.exceptions.IdeaPartTypeNotFoundException;
import com.ideahub.exceptions.UserDoesntOwnIdeaException;
import com.ideahub.exceptions.UserDoesntOwnIdeaPartException;
import com.ideahub.exceptions.UserNotAllowedToCreateMultipleIdeaPartsOfTypeException;
import com.ideahub.model.Idea;
import com.ideahub.model.IdeaPart;
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

    private final IdeaDefinitionCache ideaDefinitionCache;
    private final IdeaDAO ideaDAO;
    private final IdeaPartDAO ideaPartDAO;

    @GET
    @Path("/definition")
    @Timed
    @ExceptionMetered
    @Consumes(MediaType.APPLICATION_JSON)
    @UnitOfWork
    public List<IdeaPartType> getIdeaDefinition(@Auth final User authenticatedUser) throws Exception {
        return ideaDefinitionCache.getIdeaDefinition();
    }

    @GET
    @Path("/{ideaId}")
    @Timed
    @ExceptionMetered
    @Consumes(MediaType.APPLICATION_JSON)
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
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @UnitOfWork
    public Idea createIdea(/*@Auth final User authenticatedUser*/) throws Exception {
        Idea idea = new Idea();

        // TODO: Update this to use the authenticated user
        idea.setUserId(1L);
        // idea.setUserId(authenticatedUser.getId());
        return ideaDAO.create(idea);
    }

    @PUT
    @Path("/part")
    @Timed
    @ExceptionMetered
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @UnitOfWork
    public List<IdeaPart> updateIdeaParts(/*@Auth final User authenticatedUser,*/ final List<IdeaPart> ideaParts)
            throws UserDoesntOwnIdeaPartException, UserNotAllowedToCreateMultipleIdeaPartsOfTypeException, IdeaPartTypeNotFoundException {
        // TODO: This is inefficient, batch calls to the DB where possible
        for (IdeaPart ideaPart : ideaParts) {
            // TODO: Update this to use the authenticated user
            // final long userId = authenticatedUser.getId();
            final long userId = 1L;

            if (ideaPart.getId() != null && !ideaPartDAO.userOwnsIdeaPart(userId, ideaPart.getId())) {
                throw new UserDoesntOwnIdeaPartException();
            }

            // Don't allow someone to add more parts than the type allows
            if (ideaPart.getId() == null) {
                int currentTypeCount = ideaPartDAO.countPartsByType(userId, ideaPart.getIdeaPartTypeId());

                if (!ideaDefinitionCache.isPartTypeAllowedMultiple(ideaPart.getIdeaPartTypeId()) && currentTypeCount > 0) {
                    throw new UserNotAllowedToCreateMultipleIdeaPartsOfTypeException();
                }
            }
            
            ideaPart = ideaPartDAO.createOrUpdateIdeaPart(ideaPart);
        }

        return ideaParts;
    }
}