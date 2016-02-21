package com.ideahub.dao;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

import com.google.common.base.Optional;
import com.ideahub.model.IdeaPartVote;

import io.dropwizard.hibernate.AbstractDAO;
import jodd.petite.meta.PetiteBean;
import jodd.petite.meta.PetiteInject;

@PetiteBean
public class IdeaPartVoteDAO extends AbstractDAO<IdeaPartVote> {
    @PetiteInject
    public IdeaPartVoteDAO(final SessionFactory sessionFactory) {
        super(sessionFactory);
    }
        
    public boolean hasUserVotedOnPart(final long userId, final long ideaPartId) {
        Criteria criteria = this.criteria()
                .add(Restrictions.eq("userId", userId))
                .add(Restrictions.eq("ideaPartId", ideaPartId));
        
        return this.uniqueResult(criteria) != null;
    }
    
    public Optional<IdeaPartVote> voteOnPart(final IdeaPartVote ideaPartVote) {
        return Optional.fromNullable(this.persist(ideaPartVote));
    }
}
