package com.ideahub.resources;

import java.net.URI;

import javax.annotation.security.PermitAll;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import com.github.scribejava.core.model.Token;
import com.google.common.base.Optional;
import com.google.common.net.HttpHeaders;
import com.ideahub.dao.UserDAO;
import com.ideahub.model.User;
import com.ideahub.services.AuthenticationService;

import io.dropwizard.auth.Auth;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.hibernate.UnitOfWork;

import jodd.petite.meta.PetiteBean;
import lombok.AllArgsConstructor;

@Path("/auth")
@PetiteBean
@AllArgsConstructor
public class AuthenticationResource {
    private final UserDAO userDAO;
    private final AuthenticationService authenticationService;

    @GET
    @Timed
    @ExceptionMetered
    @Consumes
    @UnitOfWork
    public Response login() throws Exception {
        return Response
                .temporaryRedirect(URI.create(this.authenticationService.getAuthorizationUrl()))
                .build();
    }

    @GET
    @Path("/authorized")
    @Timed
    @ExceptionMetered
    @Consumes
    @UnitOfWork
    public Response authorizedCallback(@QueryParam("code") final String code)
            throws AuthenticationException {
        final Token accessToken = this.authenticationService.getToken(code);

        final com.ideahub.model.github.User githubUser = this.authenticationService
                .getUser(accessToken);
        final String email = this.authenticationService.getEmail(accessToken);

        final Optional<User> existingUser = this.userDAO.findByEmail(email);

        final String sessionToken = this.authenticationService.generateSessionToken();
        User user;
        if (existingUser.isPresent()) {
            user = existingUser.get();
            user.setOauthToken(sessionToken);
        } else {
            user = User.builder()
                    .email(email)
                    .username(githubUser.getLogin())
                    .avatarUrl(githubUser.getAvatarUrl())
                    .oauthToken(this.authenticationService.generateSessionToken())
                    .build();
        }
        this.userDAO.save(user);

        return Response.ok().header(HttpHeaders.AUTHORIZATION, user.getOauthToken()).build();
    }

    @DELETE
    @Timed
    @ExceptionMetered
    @Consumes
    @UnitOfWork
    @PermitAll
    public Response logout(@Auth final User authenticatedUser) {
        final User user = this.userDAO.findById(authenticatedUser.getId()).get();
        user.setOauthToken(null);
        this.userDAO.save(user);
        return Response.ok().build();
    }
}