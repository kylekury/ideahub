package com.ideahub.resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.annotation.JsonView;
import com.google.common.base.Optional;
import com.ideahub.dao.UserDAO;
import com.ideahub.model.User;
import com.ideahub.model.views.SelfView;
import com.ideahub.model.views.SomeoneElseView;

import io.dropwizard.auth.Auth;
import io.dropwizard.hibernate.UnitOfWork;

import jodd.petite.meta.PetiteBean;
import lombok.AllArgsConstructor;

@Path("/user")
@PetiteBean
@AllArgsConstructor
@Consumes
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {
    private final UserDAO userDAO;

    @GET
    @Timed
    @ExceptionMetered
    @UnitOfWork
    @JsonView(SelfView.class)
    public Optional<User> getSelf(@Auth final User authenticatedUser) {
        return this.userDAO.findById(authenticatedUser.getId());
    }

    @GET
    @Path("/{id}")
    @Timed
    @ExceptionMetered
    @UnitOfWork
    @JsonView(SomeoneElseView.class)
    public Optional<User> getUser(@PathParam("id") final long id) {
        return this.userDAO.findById(id);
    }
}