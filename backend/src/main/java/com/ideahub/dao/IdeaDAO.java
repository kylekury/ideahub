package com.ideahub.dao;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;

import com.ideahub.model.Idea;

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
    
    public Idea create(Idea idea) {
        return this.persist(idea);
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
        boolean result = false;
        Criteria criteria = this.criteria()
                .add(Restrictions.eq("id", ideaId))
                .add(Restrictions.eq("userId", userId));
        
        final Idea foundIdea = this.uniqueResult(criteria);
        if (foundIdea != null) {
            this.currentSession().delete(foundIdea);
            
            result = true;
        }
        return result;
    }
}
