package com.ideahub.resources.idea;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import com.google.common.base.Optional;
import com.ideahub.cache.IdeaDefinitionCache;
import com.ideahub.dao.IdeaDAO;
import com.ideahub.dao.IdeaPartDAO;
import com.ideahub.dao.IdeaPartVoteDAO;
import com.ideahub.exceptions.IdeaPartTypeNotFoundException;
import com.ideahub.exceptions.UserAlreadyVotedOnIdeaPartSuggestionException;
import com.ideahub.exceptions.UserDoesntOwnIdeaException;
import com.ideahub.exceptions.UserDoesntOwnIdeaPartException;
import com.ideahub.exceptions.UserNotAllowedToCreateMultipleIdeaPartsOfTypeException;
import com.ideahub.model.Idea;
import com.ideahub.model.IdeaPart;
import com.ideahub.model.IdeaPartVote;

import io.dropwizard.hibernate.UnitOfWork;
import jodd.petite.meta.PetiteBean;
import lombok.AllArgsConstructor;

@Path("/idea/part")
@PetiteBean
@AllArgsConstructor
public class IdeaPartResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(IdeaPartResource.class);

    private final IdeaDefinitionCache ideaDefinitionCache;
    private final IdeaDAO ideaDAO;
    private final IdeaPartDAO ideaPartDAO;
    private final IdeaPartVoteDAO ideaPartVoteDAO;

    @GET
    @Path("/{ideaPartId}")
    @Timed
    @ExceptionMetered
    @Produces(MediaType.APPLICATION_JSON)
    @UnitOfWork
    public Optional<IdeaPart> getIdeaPart(@PathParam("ideaPartId") final long ideaPartId) throws Exception {
        // TODO: Replace this with user lookup, anonymous view is fine as well.
        // final long userId = authenticatedUser.getId();
        final long userId = 1L;

        Optional<IdeaPart> ideaPart = ideaPartDAO.findById(ideaPartId);

        if (!ideaPart.isPresent()) {
            return ideaPart;
        }

        Optional<Idea> idea = ideaDAO.findById(ideaPart.get().getIdeaId());

        // TODO: There needs to be an OR condition here that also checks whether they're a collaborator
        if (idea.isPresent() && idea.get().isPrivate() && idea.get().getUserId() != userId) {
            throw new UserDoesntOwnIdeaException();
        }

        Hibernate.initialize(ideaPart.get().getIdeaPartSuggestions());

        return ideaPart;
    }

    @PUT
    @Timed
    @ExceptionMetered
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @UnitOfWork
    public List<IdeaPart> updateIdeaParts(/* @Auth final User authenticatedUser, */ final List<IdeaPart> ideaParts)
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
                if (!ideaDefinitionCache.isPartTypeAllowedMultiple(ideaPart.getIdeaPartTypeId())) {
                    int currentTypeCount = ideaPartDAO.countPartsByType(userId, ideaPart.getIdeaPartTypeId());

                    if (currentTypeCount > 0) {
                        throw new UserNotAllowedToCreateMultipleIdeaPartsOfTypeException();
                    }
                }
            }

            ideaPart = ideaPartDAO.createOrUpdateIdeaPart(ideaPart);
        }

        return ideaParts;
    }

    @DELETE
    @Path("/{ideaPartId}")
    @Timed
    @ExceptionMetered
    @Produces(MediaType.APPLICATION_JSON)
    @UnitOfWork
    public boolean deleteIdeaPart(/* @Auth final User authenticatedUser */ @PathParam("ideaPartId") final long ideaPartId) throws Exception {
        // TODO: Update this to use the authenticated user
        final long userId = 1L;
        // final long userId = authenticatedUser.getId();

        return ideaPartDAO.delete(userId, ideaPartId);
    }

    @PUT
    @Path("/{ideaPartId}/upvote")
    @Timed
    @ExceptionMetered
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @UnitOfWork
    public Optional<IdeaPart> upvote(/* @Auth final User authenticatedUser, */ @PathParam("ideaPartId") final long ideaPartId)
            throws Exception {
        return vote(ideaPartId, 1);
    }

    @PUT
    @Path("/{ideaPartId}/downvote")
    @Timed
    @ExceptionMetered
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @UnitOfWork
    public Optional<IdeaPart> downvote(/* @Auth final User authenticatedUser, */ @PathParam("ideaPartId") final long ideaPartId)
            throws Exception {
        return vote(ideaPartId, -1);
    }

    private Optional<IdeaPart> vote(final long ideaPartId, final int voteCount) throws Exception {
        // TODO: Update this to use the authenticated user
        // final long userId = authenticatedUser.getId();
        final long userId = 1L;

        final Optional<IdeaPart> ideaPart = ideaPartDAO.findById(ideaPartId);

        if (!ideaPart.isPresent()) {
            return ideaPart;
        }

        final Idea parentIdea = ideaDAO.findById(ideaPart.get().getIdeaId()).get();

        // TODO: If the idea is private, only collaborators may vote on any part
        // if (parentIdea.isPrivate() && !isCollaboratorOnIdea)

        if (!parentIdea.isPrivate()) {
            if (ideaPartVoteDAO.hasUserVotedOnPart(userId, ideaPartId)) {
                throw new UserAlreadyVotedOnIdeaPartSuggestionException();
            }

            ideaPartVoteDAO.voteOnPart(IdeaPartVote.builder()
                    .userId(userId)
                    .ideaId(parentIdea.getId())
                    .ideaPartId(ideaPartId)
                    .voteCount(voteCount).build());

            return ideaPartDAO.vote(ideaPartId, voteCount);
        }

        return ideaPart;
    }
}