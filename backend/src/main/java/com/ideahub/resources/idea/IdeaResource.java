package com.ideahub.resources.idea;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.annotation.JsonView;
import com.google.common.base.Optional;
import com.ideahub.cache.IdeaFeedCache;
import com.ideahub.cache.IdeaFeedCache.IdeaFeedType;
import com.ideahub.dao.IdeaDAO;
import com.ideahub.exceptions.UserDoesntOwnIdeaException;
import com.ideahub.model.Idea;
import com.ideahub.model.IdeaPart;
import com.ideahub.model.User;
import com.ideahub.model.hack.NewIdea;
import com.ideahub.model.views.IdeaFeedView;

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
    private static final int MAX_NUMBER_OF_RECENT_IDEAS = 100;
    private static final int MAX_NUMBER_OF_POPULAR_IDEAS = 100;

    private final IdeaDAO ideaDAO;
    private final IdeaFeedCache ideaFeedCache;

    @GET
    @Path("/{ideaId}")
    @Timed
    @ExceptionMetered
    @Produces(MediaType.APPLICATION_JSON)
    @UnitOfWork
    public Optional<Idea> getIdea(/*@Auth final User authenticatedUser,*/ @PathParam("ideaId") final long ideaId) throws Exception {
        final Optional<Idea> idea = this.ideaDAO.findById(ideaId);

        // TODO: There needs to be an OR condition here that also checks whether they're a collaborator
//        if (idea.isPresent() && idea.get().isPrivate() && idea.get().getUserId() != authenticatedUser.getId()) {
//            throw new UserDoesntOwnIdeaException();
//        }

        return idea;
    }

    @GET
    @Timed
    @ExceptionMetered
    @Produces(MediaType.APPLICATION_JSON)
    @UnitOfWork
    public Set<Idea> getIdea(@Auth final User authenticatedUser) {
        return this.ideaDAO.findListByUserId(authenticatedUser.getId());
    }

    @POST
    @Timed
    @ExceptionMetered
    @Produces(MediaType.APPLICATION_JSON)
    @UnitOfWork
    public Idea createIdea(@Auth final User authenticatedUser, final NewIdea ideaContent) throws Exception {
        Idea idea = new Idea();

        idea.setCreatedAt(new Date());
        // Just in case the user tries to create an idea under someone else's account
        idea.setUserId(authenticatedUser.getId());
        idea.setPrivate(ideaContent.isPrivate());

        // TODO: This is not ideal as we have to double-save here *sigh
        idea = this.ideaDAO.createOrUpdate(idea);

        final Map<Integer, IdeaPart> defaultParts = new HashMap<>();
        defaultParts.put(1, IdeaPart.builder().ideaId(idea.getId()).content(ideaContent.getName()).ideaPartTypeId(1).upvotes(0).downvotes(0).build());
        defaultParts.put(2, IdeaPart.builder().ideaId(idea.getId()).content(ideaContent.getElevatorPitch()).ideaPartTypeId(2).upvotes(0).downvotes(0).build());

        idea.setIdeaParts(defaultParts);

        return this.ideaDAO.createOrUpdate(idea);
    }

    @DELETE
    @Path("/{ideaId}")
    @Timed
    @ExceptionMetered
    @Produces(MediaType.APPLICATION_JSON)
    @UnitOfWork
    public boolean deleteIdea(@Auth final User authenticatedUser, @PathParam("ideaId") final long ideaId) throws Exception {
        return this.ideaDAO.delete(authenticatedUser.getId(), ideaId);
    }

    @GET
    @Path("/recent")
    @Timed
    @ExceptionMetered
    @UnitOfWork
    @JsonView(IdeaFeedView.class)
    public Set<Idea> getRecentIdeas(@QueryParam("total") final Optional<Integer> total) throws Exception {
        final int totalParameter = total.isPresent() ? Math.min(total.get(), MAX_NUMBER_OF_RECENT_IDEAS) : MAX_NUMBER_OF_RECENT_IDEAS;

        return this.ideaFeedCache.getIdeaFeed(IdeaFeedType.RECENT, totalParameter);
    }

    @GET
    @Path("/popular")
    @Timed
    @ExceptionMetered
    @Produces(MediaType.APPLICATION_JSON)
    @UnitOfWork
    @JsonView(IdeaFeedView.class)
    public Set<Idea> getPopularIdeas(@QueryParam("total") final Optional<Integer> total) throws Exception {
        final int totalParameter = total.isPresent() ? Math.min(total.get(), MAX_NUMBER_OF_POPULAR_IDEAS) : MAX_NUMBER_OF_POPULAR_IDEAS;

        // TODO: If pagination is supplied, then we should go straight to the DB
        return this.ideaFeedCache.getIdeaFeed(IdeaFeedType.POPULAR, totalParameter);
    }

    @PUT
    @Path("/{ideaId}/private/{isPrivate}")
    @Timed
    @ExceptionMetered
    @Produces(MediaType.APPLICATION_JSON)
    @UnitOfWork
    public Optional<Idea> updateIdeaPrivacy(@Auth final User authenticatedUser, @PathParam("ideaId") final long ideaId,
            @PathParam("isPrivate") final boolean isPrivate) throws Exception {
        // TODO: Eventually we'll need to check the user's account subscription to see if they
        // are allowed to have any more private ideas.
        final Optional<Idea> idea = this.ideaDAO.findById(ideaId);

        if (!idea.isPresent()) {
            return idea;
        }

        if (idea.get().getUserId() != authenticatedUser.getId()) {
            throw new UserDoesntOwnIdeaException();
        }

        idea.get().setPrivate(isPrivate);

        this.ideaDAO.createOrUpdate(idea.get());

        return idea;
    }
}