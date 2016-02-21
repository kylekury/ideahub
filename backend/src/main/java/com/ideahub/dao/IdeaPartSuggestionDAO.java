package com.ideahub.dao;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

import com.google.common.base.Optional;
import com.ideahub.model.IdeaPart;
import com.ideahub.model.IdeaPartSuggestion;

import io.dropwizard.hibernate.AbstractDAO;
import jodd.petite.meta.PetiteBean;
import jodd.petite.meta.PetiteInject;

@PetiteBean
public class IdeaPartSuggestionDAO extends AbstractDAO<IdeaPartSuggestion> {
    @PetiteInject
    public IdeaPartSuggestionDAO(final SessionFactory sessionFactory) {
        super(sessionFactory);
    }
    
    public IdeaPartSuggestion createOrUpdateIdeaPartSuggestion(IdeaPartSuggestion ideaPartSuggestion) {
        return createOrUpdateIdeaPartSuggestion(ideaPartSuggestion, false);
    }
    
    public IdeaPartSuggestion createOrUpdateIdeaPartSuggestion(IdeaPartSuggestion ideaPartSuggestion, final boolean allowVoteOverride) {
        if (ideaPartSuggestion.getId() == null) {
            ideaPartSuggestion.setDownvotes(0);
            ideaPartSuggestion.setUpvotes(0);
            
            return this.persist(ideaPartSuggestion);
        }
        
        // Hibernate is a POS when it comes to partial updates so we have to write our own :/
        IdeaPartSuggestion cleanPartSuggestion = this.get(ideaPartSuggestion.getId());
        
        ideaPartSuggestion.setUserId(cleanPartSuggestion.getUserId());
        ideaPartSuggestion.setIdeaPartId(cleanPartSuggestion.getIdeaPartId());
                
        if (ideaPartSuggestion.getUpvotes() == null || !allowVoteOverride) {
            ideaPartSuggestion.setUpvotes(cleanPartSuggestion.getUpvotes());
        }
        
        if (ideaPartSuggestion.getDownvotes() == null || !allowVoteOverride) {
            ideaPartSuggestion.setDownvotes(cleanPartSuggestion.getDownvotes());
        }
        
        if (ideaPartSuggestion.getSuggestion() == null) {
            ideaPartSuggestion.setSuggestion(cleanPartSuggestion.getSuggestion());
        }
                
        return (IdeaPartSuggestion)this.currentSession().merge(ideaPartSuggestion);
    }
    
    public boolean userOwnsIdeaPartSuggestion(final long userId, final long ideaPartSuggestionId) {
        final Criteria criteria = this.criteria()
                .add(Restrictions.eq("userId", userId))
                .add(Restrictions.eq("id", ideaPartSuggestionId));
        
        return Optional.fromNullable(this.uniqueResult(criteria)).isPresent();
    }
        
    public Optional<IdeaPartSuggestion> findById(final long ideaPartSuggestionId) {
        Criteria criteria = this.criteria()
                .add(Restrictions.eq("id", ideaPartSuggestionId));
        
        return Optional.fromNullable(this.uniqueResult(criteria));
    }
    
    public boolean delete(final long userId, final long ideaPartSuggestionId) {
        Criteria criteria = this.criteria()
                .add(Restrictions.eq("id", ideaPartSuggestionId))
                .add(Restrictions.eq("userId", userId));
        
        final IdeaPartSuggestion foundPartSuggestion = this.uniqueResult(criteria);

        if (foundPartSuggestion != null) {
            this.currentSession().delete(foundPartSuggestion);
            
            return true;
        }
        
        return false;
    }
    
    public Optional<IdeaPartSuggestion> vote(final long ideaPartSuggestionId, final int voteCount) {
        Optional<IdeaPartSuggestion> foundPartSuggestion = findById(ideaPartSuggestionId);
        
        if (!foundPartSuggestion.isPresent()) {
            return foundPartSuggestion;
        }
        
        // TODO: Test if this is thread-safe
        if (voteCount > 0) {
            foundPartSuggestion.get().setUpvotes(foundPartSuggestion.get().getUpvotes() + voteCount);
        } else {
            foundPartSuggestion.get().setDownvotes(foundPartSuggestion.get().getDownvotes() + Math.abs(voteCount));
        }
        
        
        createOrUpdateIdeaPartSuggestion(foundPartSuggestion.get(), true);
        
        return foundPartSuggestion;
    }
    
    public int countSuggestionsByIdeaId(final long ideaId) {
        Criteria criteria = this.criteria()
                .add(Restrictions.eqOrIsNull("ideaId", ideaId));
        
        return criteria.list().size();
    }
}
