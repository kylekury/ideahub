package com.ideahub.resources;

import java.net.URI;
import java.util.List;

import javax.annotation.security.PermitAll;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.shiro.crypto.RandomNumberGenerator;
import org.apache.shiro.crypto.hash.Sha512Hash;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.eclipse.jetty.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Token;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.model.Verifier;
import com.github.scribejava.core.oauth.OAuth20Service;
import com.google.common.base.Optional;
import com.google.common.net.HttpHeaders;
import com.ideahub.dao.UserDAO;
import com.ideahub.model.User;
import com.ideahub.model.github.Email;

import io.dropwizard.auth.Auth;
import io.dropwizard.hibernate.UnitOfWork;

import jodd.petite.meta.PetiteBean;
import lombok.AllArgsConstructor;

@Path("/auth")
@PetiteBean
@AllArgsConstructor
public class AuthenticationResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationResource.class);
    private static final String USER_RESOURCE_URL = "https://api.github.com/user";
    private static final String EMAIL_RESOURCE_URL = "https://api.github.com/user/emails";
    private static final ObjectMapper MAPPER = new ObjectMapper().setPropertyNamingStrategy(
            PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    private final UserDAO userDAO;
    private final OAuth20Service oauthService;
    private final RandomNumberGenerator randomNumberGenerator;

    @GET
    @Path("/login")
    @Timed
    @ExceptionMetered
    @Consumes
    @UnitOfWork
    public Response login() throws Exception {
        return Response.temporaryRedirect(URI.create(this.oauthService.getAuthorizationUrl()))
                .build();
    }

    @GET
    @Path("/authorized")
    @Timed
    @ExceptionMetered
    @Consumes
    @UnitOfWork
    public Response authorizedCallback(@QueryParam("code") final String code) throws Exception {
        final Verifier verifier = new Verifier(code);
        final Token accessToken = this.oauthService.getAccessToken(verifier);
        final OAuthRequest request = new OAuthRequest(Verb.GET, USER_RESOURCE_URL,
                this.oauthService);
        this.oauthService.signRequest(accessToken, request);
        final com.github.scribejava.core.model.Response response = request.send();

        if (response.getCode() != HttpStatus.OK_200) {
            LOGGER.warn("Failed authentication: " + response.getBody());
            return Response.status(Status.BAD_REQUEST).build();
        }
        final com.ideahub.model.github.User githubUser = MAPPER.readValue(response.getBody(),
                com.ideahub.model.github.User.class);

        final OAuthRequest requestEmail = new OAuthRequest(Verb.GET, EMAIL_RESOURCE_URL,
                this.oauthService);
        this.oauthService.signRequest(accessToken, requestEmail);
        final com.github.scribejava.core.model.Response responseEmail = requestEmail.send();

        if (responseEmail.getCode() != HttpStatus.OK_200) {
            return Response.status(Status.BAD_REQUEST).build();
        }
        final List<Email> emails = MAPPER.readValue(responseEmail.getBody(),
                new TypeReference<List<Email>>() {
                });

        // TODO(mitsuo): Handle missing emails case.
        final Optional<User> existingUser = this.userDAO.findByEmail(emails.get(0).getEmail());
        // Creating our own token
        final SimpleHash sessionToken = new Sha512Hash(this.randomNumberGenerator.nextBytes());

        User user;
        if (existingUser.isPresent()) {
            user = existingUser.get();
        } else {
            user = User.builder()
                    // TODO(mitsuo): Handle missing emails case.
                    .email(emails.get(0).getEmail())
                    .username(githubUser.getLogin())
                    .oauthToken(sessionToken.toBase64())
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
        if (authenticatedUser != null) {
            final User user = this.userDAO.findById(authenticatedUser.getId()).get();
            user.setOauthToken(null);
            this.userDAO.save(user);
            return Response.ok().build();
        } else {
            return Response.status(Status.BAD_REQUEST).build();
        }
    }
}