package com.ideahub.resources.idea;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import com.google.common.base.Optional;
import com.ideahub.dao.IdeaDAO;
import com.ideahub.exceptions.UserDoesntOwnIdeaException;
import com.ideahub.model.Idea;
import com.ideahub.model.User;

import io.dropwizard.auth.Auth;
import io.dropwizard.hibernate.UnitOfWork;

import jodd.petite.meta.PetiteBean;
import lombok.AllArgsConstructor;

@Path("/idea")
@PetiteBean
@AllArgsConstructor
@Produces(MediaType.APPLICATION_JSON)
public class IdeaResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(IdeaResource.class);
    private static final int MAX_NUMBER_OF_RECENT_IDEAS = 100;

    private final IdeaDAO ideaDAO;

    @GET
    @Path("/{ideaId}")
    @Timed
    @ExceptionMetered
    @Produces(MediaType.APPLICATION_JSON)
    @UnitOfWork
    public Optional<Idea> getIdea(@Auth final User authenticatedUser, @PathParam("ideaId") final long ideaId) throws Exception {
        final Optional<Idea> idea = this.ideaDAO.findById(ideaId);

        // TODO: There needs to be an OR condition here that also checks whether they're a collaborator
        if (idea.isPresent() && idea.get().isPrivate() && idea.get().getUserId() != authenticatedUser.getId()) {
            throw new UserDoesntOwnIdeaException();
        }

        return idea;
    }

    @POST
    @Timed
    @ExceptionMetered
    @Produces(MediaType.APPLICATION_JSON)
    @UnitOfWork
    public Idea createIdea(@Auth final User authenticatedUser) throws Exception {
        final Idea idea = new Idea();

        idea.setCreatedAt(new Date());
        // Just in case the user tries to create an idea under someone else's account
        idea.setUserId(authenticatedUser.getId());
        idea.setPrivate(false);

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
    public Set<Idea> getRecentIdeas(@QueryParam("total") final Optional<Integer> total) {
        int totalParameter;
        if (!total.isPresent()) {
            totalParameter = MAX_NUMBER_OF_RECENT_IDEAS;
        } else {
            totalParameter = total.get();
        }
        final Set<Idea> ideas = new HashSet<>(this.ideaDAO.findRecent(Math.min(totalParameter, MAX_NUMBER_OF_RECENT_IDEAS)));
        return ideas;
    }

    @PUT
    @Path("/{ideaId}/private/{isPrivate}")
    @Timed
    @ExceptionMetered
    @Produces(MediaType.APPLICATION_JSON)
    @UnitOfWork
    public Optional<Idea> updateIdeaPrivacy(@Auth final User authenticatedUser, @PathParam("ideaId") final long ideaId, @PathParam("isPrivate") final boolean isPrivate) throws Exception {
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

    @GET
    @Path("/popular")
    @Timed
    @ExceptionMetered
    @Produces(MediaType.APPLICATION_JSON)
    @UnitOfWork
    public List<Idea> getPopularIdeas() throws Exception {
        return this.ideaDAO.findPopularIdeas();
    }
}