package com.ideahub.resources;

import java.util.List;

import javax.annotation.security.PermitAll;
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
import com.ideahub.dao.IdeaPartTypeDAO;
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
@Consumes
@Produces(MediaType.APPLICATION_JSON)
public class IdeaResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(IdeaResource.class);

    private final IdeaDefinitionCache ideaDefinitionCache;
    private final IdeaDAO ideaDAO;
    private final IdeaPartDAO ideaPartDAO;
    private final IdeaPartTypeDAO ideaPartTypeDAO;

    @GET
    @Path("/definition")
    @Timed
    @ExceptionMetered
    @UnitOfWork
    @PermitAll
    public List<IdeaPartType> getIdeaDefinition(@Auth final User authenticatedUser)
            throws Exception {
        return this.ideaPartTypeDAO.getIdeaDefinition();
    }

    @GET
    @Path("/{ideaId}")
    @Timed
    @ExceptionMetered
    @Produces(MediaType.APPLICATION_JSON)
    @UnitOfWork
    public Optional<Idea> getIdea(@Auth final User user, @PathParam("ideaId") final long ideaId)
            throws Exception {
        final Optional<Idea> idea = this.ideaDAO.findById(ideaId);

        // TODO: There needs to be an OR condition here that also checks whether
        // they're a collaborator
        if (idea.isPresent() && idea.get().isPrivate() && idea.get().getUserId() != user.getId()) {
            throw new UserDoesntOwnIdeaException();
        }

        return idea;
    }

    @POST
    @Timed
    @ExceptionMetered
    @Consumes
    @Produces(MediaType.APPLICATION_JSON)
    @UnitOfWork
    public Idea createIdea(/* @Auth final User authenticatedUser */) throws Exception {
        final Idea idea = new Idea();

        // TODO: Update this to use the authenticated user
        idea.setUserId(1L);
        // idea.setUserId(authenticatedUser.getId());
        return this.ideaDAO.create(idea);
    }

    @PUT
    @Path("/part")
    @Timed
    @ExceptionMetered
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @UnitOfWork
    public List<IdeaPart> updateIdeaParts(/*
                                           * @Auth final User authenticatedUser,
                                           */ final List<IdeaPart> ideaParts)
            throws UserDoesntOwnIdeaPartException,
            UserNotAllowedToCreateMultipleIdeaPartsOfTypeException, IdeaPartTypeNotFoundException {
        // TODO: This is inefficient, batch calls to the DB where possible
        for (IdeaPart ideaPart : ideaParts) {
            // TODO: Update this to use the authenticated user
            // final long userId = authenticatedUser.getId();
            final long userId = 1L;

            if (ideaPart.getId() != null
                    && !this.ideaPartDAO.userOwnsIdeaPart(userId, ideaPart.getId())) {
                throw new UserDoesntOwnIdeaPartException();
            }

            // Don't allow someone to add more parts than the type allows
            if (ideaPart.getId() == null) {
                if (!this.ideaDefinitionCache.isPartTypeAllowedMultiple(ideaPart.getIdeaPartTypeId())) {
                    final int currentTypeCount = this.ideaPartDAO.countPartsByType(userId,
                            ideaPart.getIdeaPartTypeId());

                    if (currentTypeCount > 0) {
                        throw new UserNotAllowedToCreateMultipleIdeaPartsOfTypeException();
                    }
                }
            }

            ideaPart = this.ideaPartDAO.createOrUpdateIdeaPart(ideaPart);
        }

        return ideaParts;
    }
}