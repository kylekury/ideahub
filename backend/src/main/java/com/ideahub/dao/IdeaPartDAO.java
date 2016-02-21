package com.ideahub.dao;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

import com.google.common.base.Optional;
import com.ideahub.model.IdeaPart;

import io.dropwizard.hibernate.AbstractDAO;
import jodd.petite.meta.PetiteBean;
import jodd.petite.meta.PetiteInject;

@PetiteBean
public class IdeaPartDAO extends AbstractDAO<IdeaPart> {
    @PetiteInject
    public IdeaPartDAO(final SessionFactory sessionFactory) {
        super(sessionFactory);
    }
    
    public IdeaPart createOrUpdateIdeaPart(final IdeaPart ideaPart) {
        if (ideaPart.getId() == null) {
            return this.persist(ideaPart);
        }
        
        return (IdeaPart)this.currentSession().merge(ideaPart);
    }
    
    public boolean userOwnsIdeaPart(final long userId, final long ideaPartId) {
        final Criteria criteria = this.criteria()
                .add(Restrictions.eq("userId", userId))
                .add(Restrictions.eq("id", ideaPartId));
        
        return Optional.fromNullable(this.uniqueResult(criteria)).isPresent();
    }
    
    public int countPartsByType(final long userId, final int ideaPartTypeId) {
        final Criteria criteria = this.criteria()
                .add(Restrictions.eq("userId", userId))
                .add(Restrictions.eq("ideaPartTypeId", ideaPartTypeId));
        
        return this.list(criteria).size();
    }
}
