package com.ideahub.dao;
// Generated Feb 20, 2016 12:32:19 AM by Hibernate Tools 4.3.1

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.context.internal.ManagedSessionContext;
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
    private final SessionFactory sessionFactory;

    @PetiteInject
    public UserDAO(final SessionFactory sessionFactory) {
        super(sessionFactory);
        this.sessionFactory = sessionFactory;
    }

    public Optional<User> findByEmail(final String email) {
        final Criteria criteria = this.criteria()
                .add(Restrictions.eq("email", email));
        return Optional.fromNullable(this.uniqueResult(criteria));
    }

    public void save(final User user) {
        this.persist(user);
    }

    /**
     * Opens a session and binds it to hibernate context. The session must be
     * closed when the task is done.
     *
     * @return The just opened session.
     */
    public Session openSession() {
        final Session session = this.sessionFactory.openSession();
        ManagedSessionContext.bind(session);
        return session;
    }

    public Optional<User> openSessionAndFindByOAuthToken(final String token) {
        final Session session = this.openSession();
        try {
            final Criteria criteria = this.criteria()
                    .add(Restrictions.eq("oauthToken", token));
            return Optional.fromNullable(this.uniqueResult(criteria));
        } finally {
            session.close();
        }
    }

    public Optional<User> findById(final long id) {
        final Criteria criteria = this.criteria()
                .add(Restrictions.eq("id", id));
        return Optional.fromNullable(this.uniqueResult(criteria));
    }
}