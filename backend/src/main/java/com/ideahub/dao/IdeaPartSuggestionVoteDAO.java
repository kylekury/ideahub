package com.ideahub.dao;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

import com.google.common.base.Optional;
import com.ideahub.model.IdeaPartSuggestionVote;

import io.dropwizard.hibernate.AbstractDAO;
import jodd.petite.meta.PetiteBean;
import jodd.petite.meta.PetiteInject;

@PetiteBean
public class IdeaPartSuggestionVoteDAO extends AbstractDAO<IdeaPartSuggestionVote> {
    @PetiteInject
    public IdeaPartSuggestionVoteDAO(final SessionFactory sessionFactory) {
        super(sessionFactory);
    }
        
    public boolean hasUserVotedOnPartSuggestion(final long userId, final long ideaPartSuggestionId) {
        Criteria criteria = this.criteria()
                .add(Restrictions.eq("userId", userId))
                .add(Restrictions.eq("ideaPartSuggestionId", ideaPartSuggestionId));
        
        return this.uniqueResult(criteria) != null;
    }
    
    public Optional<IdeaPartSuggestionVote> voteOnPartSuggestion(final IdeaPartSuggestionVote ideaPartSuggestionVote) {
        return Optional.fromNullable(this.persist(ideaPartSuggestionVote));
    }
}
