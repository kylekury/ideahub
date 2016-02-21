package com.ideahub.auth;

import com.ideahub.model.User;

import io.dropwizard.auth.Authorizer;

import jodd.petite.meta.PetiteBean;

@PetiteBean
public class UserRoleAuthorizer implements Authorizer<User> {
    @Override
    public boolean authorize(final User principal, final String role) {
//        return principal.getRole().equalsIgnoreCase(role);
        return true;
    }
}