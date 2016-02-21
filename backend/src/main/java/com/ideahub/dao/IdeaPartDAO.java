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
    
    public IdeaPart createOrUpdateIdeaPart(IdeaPart ideaPart) {
        return createOrUpdateIdeaPart(ideaPart, false);
    }
    
    public IdeaPart createOrUpdateIdeaPart(IdeaPart ideaPart, boolean allowVoteOverride) {
        if (ideaPart.getId() == null) {
            ideaPart.setDownvotes(0);
            ideaPart.setUpvotes(0);
            
            return this.persist(ideaPart);
        }
        
        // Hibernate is a POS when it comes to partial updates so we have to write our own :/
        IdeaPart cleanPart = this.get(ideaPart.getId());
        
        ideaPart.setIdeaId(cleanPart.getIdeaId());
        ideaPart.setUserId(cleanPart.getUserId());
                
        if (ideaPart.getUpvotes() == null || !allowVoteOverride) {
            ideaPart.setUpvotes(cleanPart.getUpvotes());
        }
        
        if (ideaPart.getDownvotes() == null || !allowVoteOverride) {
            ideaPart.setDownvotes(cleanPart.getDownvotes());
        }
        
        if (ideaPart.getContent() == null) {
            ideaPart.setContent(cleanPart.getContent());
        }
        
        if (ideaPart.getJustification() == null) {
            ideaPart.setJustification(cleanPart.getJustification());
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
    
    public Optional<IdeaPart> findById(final long ideaPartId) {
        Criteria criteria = this.criteria()
                .add(Restrictions.eq("id", ideaPartId));
        
        return Optional.fromNullable(this.uniqueResult(criteria));
    }
    
    public boolean delete(final long userId, final long ideaPartId) {
        Criteria criteria = this.criteria()
                .add(Restrictions.eq("id", ideaPartId))
                .add(Restrictions.eq("userId", userId));
        
        final IdeaPart foundPart = this.uniqueResult(criteria);

        if (foundPart != null) {
            this.currentSession().delete(foundPart);
            
            return true;
        }
        
        return false;
    }
    
    public Optional<IdeaPart> vote(final long ideaPartId, final int voteCount) {
        Optional<IdeaPart> foundPart = findById(ideaPartId);
        
        if (!foundPart.isPresent()) {
            return foundPart;
        }
        
        // TODO: Test if this is thread-safe
        if (voteCount > 0) {
            foundPart.get().setUpvotes(foundPart.get().getUpvotes() + voteCount);
        } else {
            foundPart.get().setDownvotes(foundPart.get().getDownvotes() + Math.abs(voteCount));
        }
        
        
        createOrUpdateIdeaPart(foundPart.get(), true);
        
        return foundPart;
    }
}
