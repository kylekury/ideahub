package com.ideahub.resources;

import java.net.URI;

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
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Token;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.model.Verifier;
import com.github.scribejava.core.oauth.OAuth20Service;
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
    private static final String PROTECTED_RESOURCE_URL = "https://api.github.com/user";

    private final UserDAO userDAO;
    private final OAuth20Service oauthService;

    @GET
    @Path("/login")
    @Timed
    @ExceptionMetered
    @Consumes
    @UnitOfWork
    public Response login(@Context final UriInfo uriInfo,
            @Auth final User authenticatedUser) throws Exception {
        return Response.temporaryRedirect(URI.create(this.oauthService.getAuthorizationUrl()))
                .build();
    }

    @GET
    @Path("/authorized")
    @Timed
    @ExceptionMetered
    @Consumes
    @UnitOfWork
    public Response authorizedCallback(@Context final UriInfo uriInfo,
            @Auth final User authenticatedUser) throws Exception {
        // TODO(mtakaki): Find the parameter where the authorization token will
        // be sent.
        final Verifier verifier = new Verifier("");
        final Token accessToken = this.oauthService.getAccessToken(verifier);
        final OAuthRequest request = new OAuthRequest(Verb.GET, PROTECTED_RESOURCE_URL,
                this.oauthService);
        this.oauthService.signRequest(accessToken, request);
        final com.github.scribejava.core.model.Response response = request.send();
        return Response.ok().build();
    }
}