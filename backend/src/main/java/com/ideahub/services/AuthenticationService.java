package com.ideahub.services;

import java.io.IOException;
import java.util.List;

import org.apache.shiro.crypto.RandomNumberGenerator;
import org.apache.shiro.crypto.hash.Sha512Hash;
import org.eclipse.jetty.http.HttpStatus;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Token;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.model.Verifier;
import com.github.scribejava.core.oauth.OAuth20Service;
import com.ideahub.model.github.Email;
import com.ideahub.model.github.User;

import io.dropwizard.auth.AuthenticationException;

import jodd.petite.meta.PetiteBean;
import lombok.AllArgsConstructor;
import lombok.Getter;

@PetiteBean
@AllArgsConstructor
public class AuthenticationService {
    private static final String USER_RESOURCE_URL = "https://api.github.com/user";
    private static final String EMAIL_RESOURCE_URL = "https://api.github.com/user/emails";
    private static final ObjectMapper MAPPER = new ObjectMapper().setPropertyNamingStrategy(
            PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    @Getter
    private final OAuth20Service oauthService;
    private final RandomNumberGenerator randomNumberGenerator;

    public Token getToken(final String code) {
        final Verifier verifier = new Verifier(code);
        return this.oauthService.getAccessToken(verifier);
    }

    public User getUser(final Token token) throws AuthenticationException {
        final OAuthRequest request = new OAuthRequest(Verb.GET, USER_RESOURCE_URL,
                this.oauthService);
        this.oauthService.signRequest(token, request);
        final Response response = request.send();

        if (response.getCode() != HttpStatus.OK_200) {
            throw new AuthenticationException("Failed authentication: " + response.getCode());
        }

        try {
            return MAPPER.readValue(response.getBody(), User.class);
        } catch (final IOException e) {
            throw new AuthenticationException("Unexpected github response: " + response.getBody());
        }
    }

    public String getEmail(final Token token) throws AuthenticationException {
        final OAuthRequest requestEmail = new OAuthRequest(Verb.GET, EMAIL_RESOURCE_URL,
                this.oauthService);
        this.oauthService.signRequest(token, requestEmail);
        final Response responseEmail = requestEmail.send();

        if (responseEmail.getCode() != HttpStatus.OK_200) {
            throw new AuthenticationException(
                    "Failed to retrieve user's email: " + responseEmail.getCode());
        }
        List<Email> emails;
        try {
            emails = MAPPER.readValue(responseEmail.getBody(),
                    new TypeReference<List<Email>>() {
                    });
        } catch (final IOException e) {
            throw new AuthenticationException(
                    "Unexpected github response: " + responseEmail.getBody());
        }

        if (emails.isEmpty()) {
            throw new AuthenticationException("User has not email registered");
        }

        return emails.stream().filter(currentEmail -> currentEmail.isPrimary()).findFirst().get()
                .getEmail();
    }

    public String generateSessionToken() {
        return new Sha512Hash(this.randomNumberGenerator.nextBytes()).toBase64();
    }

    public String getAuthorizationUrl() {
        return this.oauthService.getAuthorizationUrl();
    }
}