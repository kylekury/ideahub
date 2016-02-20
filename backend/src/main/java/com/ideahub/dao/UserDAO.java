package com.ideahub.dao;
// Generated Feb 20, 2016 12:32:19 AM by Hibernate Tools 4.3.1

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

import com.google.common.base.Optional;
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

    public Optional<User> findByEmail(final String email) {
        final Criteria criteria = this.criteria()
                .add(Restrictions.eq("email", email));
        return Optional.fromNullable(this.uniqueResult(criteria));
    }
}