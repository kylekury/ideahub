package com.ideahub.dao;
// Generated Feb 20, 2016 12:32:19 AM by Hibernate Tools 4.3.1

import org.hibernate.SessionFactory;

import com.ideahub.model.User;

import io.dropwizard.hibernate.AbstractDAO;

/**
 * Home object for domain model class User.
 *
 * @see User
 */
public class UserDAO extends AbstractDAO<User> {
    public UserDAO(final SessionFactory sessionFactory) {
        super(sessionFactory);
    }
}