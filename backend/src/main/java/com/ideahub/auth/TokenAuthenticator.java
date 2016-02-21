package com.ideahub.auth;

import com.google.common.base.Optional;
import com.ideahub.dao.UserDAO;
import com.ideahub.model.User;

import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;

import jodd.petite.meta.PetiteBean;
import lombok.AllArgsConstructor;

@PetiteBean
@AllArgsConstructor
public class TokenAuthenticator implements Authenticator<String, User> {
    private final UserDAO userDAO;

    @Override
    public Optional<User> authenticate(final String credentials) throws AuthenticationException {
        return this.userDAO.openSessionAndFindByOAuthToken(credentials);
    }
}