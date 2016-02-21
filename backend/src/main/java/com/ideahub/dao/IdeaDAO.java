package com.ideahub.dao;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;

import com.ideahub.model.Idea;
import com.ideahub.model.IdeaPart;

import io.dropwizard.hibernate.AbstractDAO;
import jodd.petite.meta.PetiteBean;
import jodd.petite.meta.PetiteInject;
import org.hibernate.criterion.Restrictions;

import com.google.common.base.Optional;

@PetiteBean
public class IdeaDAO extends AbstractDAO<Idea> {
    @PetiteInject
    public IdeaDAO(final SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public Idea createOrUpdate(Idea idea) {
        if (idea.getId() == null) {
            return this.persist(idea);
        }

        // Hibernate is a POS when it comes to partial updates so we have to write our own :/
        Idea cleanIdea = this.get(idea.getId());
        
        idea.setUserId(cleanIdea.getUserId());

        return (Idea)this.currentSession().merge(idea);
    }

    public Optional<Idea> findById(final long ideaId) {
        Criteria criteria = this.criteria()
                .add(Restrictions.eq("id", ideaId));

        return Optional.fromNullable(this.uniqueResult(criteria));
    }

    public Optional<Idea> findByUserId(Long aUserId) {
        final Criteria criteria = this.criteria()
                .add(Restrictions.idEq(aUserId));
        return Optional.fromNullable(this.uniqueResult(criteria));
    }

    public boolean delete(final long userId, final long ideaId) {
        Criteria criteria = this.criteria()
                .add(Restrictions.eq("id", ideaId))
                .add(Restrictions.eq("userId", userId));

        final Idea foundIdea = this.uniqueResult(criteria);
        if (foundIdea != null) {
            this.currentSession().delete(foundIdea);

            return true;
        }

        return false;
    }
}
