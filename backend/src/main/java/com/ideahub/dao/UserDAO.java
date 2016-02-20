package com.ideahub.dao;
// Generated Feb 20, 2016 12:32:19 AM by Hibernate Tools 4.3.1

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

import com.google.common.base.Optional;
import com.ideahub.model.User;

import io.dropwizard.hibernate.AbstractDAO;
import jodd.petite.meta.PetiteBean;
import jodd.petite.meta.PetiteInject;

/**
 * Home object for domain model class User.
 *
 * @see User
 */
@PetiteBean
public class UserDAO extends AbstractDAO<User> {
    @PetiteInject
    public UserDAO(final SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public Optional<User> findByEmail(final String email) {
        final Criteria criteria = this.criteria()
                .add(Restrictions.eq("email", email));
        return Optional.fromNullable(this.uniqueResult(criteria));
    }
}