package com.ideahub.resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import com.github.scribejava.core.oauth.OAuthService;
import com.ideahub.dao.UserDAO;
import com.ideahub.model.User;

import io.dropwizard.auth.Auth;
import io.dropwizard.hibernate.UnitOfWork;

import jodd.petite.meta.PetiteBean;
import lombok.AllArgsConstructor;

@Path("/auth")
@PetiteBean
@AllArgsConstructor
public class AuthenticationResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationResource.class);

    private final UserDAO userDAO;
    private final OAuthService oauthService;

    @GET
    @Path("/login")
    @Timed
    @ExceptionMetered
    @Consumes
    @UnitOfWork
    public Response authenticate(@Context final UriInfo uriInfo,
            @Auth final User authenticatedUser) throws Exception {
        return Response.ok().build();
    }
}