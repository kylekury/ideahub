package com.ideahub.resources.idea;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
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
import com.ideahub.dao.IdeaDAO;
import com.ideahub.dao.IdeaPartDAO;
import com.ideahub.dao.IdeaPartSuggestionDAO;
import com.ideahub.dao.IdeaPartSuggestionVoteDAO;
import com.ideahub.exceptions.IdeaPartDoesntExistException;
import com.ideahub.exceptions.UserAlreadyVotedOnIdeaPartSuggestionException;
import com.ideahub.model.Idea;
import com.ideahub.model.IdeaPart;
import com.ideahub.model.IdeaPartSuggestion;
import com.ideahub.model.IdeaPartSuggestionVote;
import com.ideahub.model.User;

import io.dropwizard.auth.Auth;
import io.dropwizard.hibernate.UnitOfWork;

import jodd.petite.meta.PetiteBean;
import lombok.AllArgsConstructor;

@Path("/idea/part/suggestion")
@PetiteBean
@AllArgsConstructor
public class IdeaPartSuggestionResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(IdeaPartSuggestionResource.class);

    private final IdeaDAO ideaDAO;
    private final IdeaPartDAO ideaPartDAO;
    private final IdeaPartSuggestionDAO ideaPartSuggestionDAO;
    private final IdeaPartSuggestionVoteDAO ideaPartSuggestionVoteDAO;

    @PUT
    @Timed
    @ExceptionMetered
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @UnitOfWork
    public IdeaPartSuggestion updateIdeaPartSuggestion(@Auth final User authenticatedUser, IdeaPartSuggestion ideaPartSuggestion) throws Exception {
        final long userId = authenticatedUser.getId();

        final Optional<IdeaPart> parentIdeaPart = this.ideaPartDAO.findById(ideaPartSuggestion.getIdeaPartId());

        if (!parentIdeaPart.isPresent()) {
            throw new IdeaPartDoesntExistException();
        }

        final Idea parentIdea = this.ideaDAO.findById(parentIdeaPart.get().getIdeaId()).get();

        // TODO: If the idea is private, only collaborators may write suggestions on a part
        // if (parentIdea.isPrivate() && !isCollaboratorOnIdea)

        if (!parentIdea.isPrivate() || parentIdeaPart.get().getUserId() == userId) {
            ideaPartSuggestion = this.ideaPartSuggestionDAO.createOrUpdateIdeaPartSuggestion(ideaPartSuggestion);
        }

        return ideaPartSuggestion;
    }

    @DELETE
    @Path("/{ideaPartSuggestionId}")
    @Timed
    @ExceptionMetered
    @Produces(MediaType.APPLICATION_JSON)
    @UnitOfWork
    public boolean deleteIdeaPartSuggestion(@Auth final User authenticatedUser, @PathParam("ideaPartSuggestionId") final long ideaPartSuggestionId)
            throws Exception {
        final long userId = authenticatedUser.getId();

        return this.ideaPartSuggestionDAO.delete(userId, ideaPartSuggestionId);
    }

    @PUT
    @Path("/{ideaPartSuggestionId}/upvote")
    @Timed
    @ExceptionMetered
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @UnitOfWork
    public Optional<IdeaPartSuggestion> upvote(@Auth final User authenticatedUser, @PathParam("ideaPartSuggestionId") final long ideaPartSuggestionId)
            throws Exception {
        return this.vote(ideaPartSuggestionId, 1, authenticatedUser);
    }

    @PUT
    @Path("/{ideaPartSuggestionId}/downvote")
    @Timed
    @ExceptionMetered
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @UnitOfWork
    public Optional<IdeaPartSuggestion> downvote(@Auth final User authenticatedUser, @PathParam("ideaPartSuggestionId") final long ideaPartSuggestionId)
            throws Exception {
        return this.vote(ideaPartSuggestionId, -1, authenticatedUser);
    }

    private Optional<IdeaPartSuggestion> vote(final long ideaPartSuggestionId, final int voteCount, final User authenticatedUser) throws Exception {
        final long userId = authenticatedUser.getId();

        final Optional<IdeaPartSuggestion> ideaPartSuggestion = this.ideaPartSuggestionDAO.findById(ideaPartSuggestionId);

        if (!ideaPartSuggestion.isPresent()) {
            return ideaPartSuggestion;
        }

        final IdeaPart parentIdeaPart = this.ideaPartDAO.findById(ideaPartSuggestion.get().getIdeaPartId()).get();
        final Idea parentIdea = this.ideaDAO.findById(parentIdeaPart.getIdeaId()).get();

        // TODO: If the idea is private, only collaborators may vote on any part suggestion
        // if (parentIdea.isPrivate() && !isCollaboratorOnIdea)

        if (!parentIdea.isPrivate()) {
            if (this.ideaPartSuggestionVoteDAO.hasUserVotedOnPartSuggestion(userId, ideaPartSuggestionId)) {
                throw new UserAlreadyVotedOnIdeaPartSuggestionException();
            }

            this.ideaPartSuggestionVoteDAO.voteOnPartSuggestion(IdeaPartSuggestionVote.builder()
                    .userId(userId)
                    .ideaId(parentIdea.getId())
                    .ideaPartId(parentIdeaPart.getId())
                    .ideaPartSuggestionId(ideaPartSuggestionId)
                    .voteCount(voteCount).build());

            return this.ideaPartSuggestionDAO.vote(ideaPartSuggestionId, voteCount);
        }

        return ideaPartSuggestion;
    }
}