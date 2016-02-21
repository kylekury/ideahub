package com.ideahub.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.google.common.base.Optional;
import com.ideahub.model.Idea;

import io.dropwizard.hibernate.AbstractDAO;

import jodd.petite.meta.PetiteBean;
import jodd.petite.meta.PetiteInject;

@PetiteBean
public class IdeaDAO extends AbstractDAO<Idea> {
    @PetiteInject
    public IdeaDAO(final SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public Idea createOrUpdate(final Idea idea) {
        if (idea.getId() == null) {
            return this.persist(idea);
        }

        // Hibernate is a POS when it comes to partial updates so we have to write our own :/
        final Idea cleanIdea = this.get(idea.getId());

        idea.setUserId(cleanIdea.getUserId());

        return (Idea)this.currentSession().merge(idea);
    }

    public Optional<Idea> findById(final long ideaId) {
        final Criteria criteria = this.criteria()
                .add(Restrictions.eq("id", ideaId));

        return Optional.fromNullable(this.uniqueResult(criteria));
    }

    public Optional<Idea> findByUserId(final Long aUserId) {
        final Criteria criteria = this.criteria()
                .add(Restrictions.idEq(aUserId));
        return Optional.fromNullable(this.uniqueResult(criteria));
    }

    public boolean delete(final long userId, final long ideaId) {
        final Criteria criteria = this.criteria()
                .add(Restrictions.eq("id", ideaId))
                .add(Restrictions.eq("userId", userId));

        final Idea foundIdea = this.uniqueResult(criteria);
        if (foundIdea != null) {
            this.currentSession().delete(foundIdea);

            return true;
        }

        return false;
    }

    public List<Idea> findRecent(final int maxResults) {
        final Criteria criteria = this.criteria()
                .addOrder(Order.desc("createdAt"))
                .setMaxResults(maxResults);
        return this.list(criteria);
    }
}