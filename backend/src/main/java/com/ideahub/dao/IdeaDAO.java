package com.ideahub.dao;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.google.common.base.Optional;
import com.ideahub.model.Idea;
import com.ideahub.model.IdeaPart;
import com.ideahub.model.IdeaPartSuggestion;
import com.ideahub.model.User;

import io.dropwizard.hibernate.AbstractDAO;

import jodd.petite.meta.PetiteBean;
import jodd.petite.meta.PetiteInject;

@PetiteBean
public class IdeaDAO extends AbstractDAO<Idea> {
    // TODO: This naughty and bad
    private final UserDAO userDAO;

    @PetiteInject
    public IdeaDAO(final SessionFactory sessionFactory, final UserDAO userDAO) {
        super(sessionFactory);
        this.userDAO = userDAO;
    }

    public Idea createOrUpdate(final Idea idea) {
        if (idea.getId() == null) {
            return this.persist(idea);
        }

        // Hibernate is a POS when it comes to partial updates so we have to write our own :/
        final Idea cleanIdea = this.get(idea.getId());

        idea.setUserId(cleanIdea.getUserId());

        return this.reconcileIdeaSummary((Idea) this.currentSession().merge(idea));
    }

    public Optional<Idea> findById(final long ideaId) {
        final Criteria criteria = this.criteria()
                .add(Restrictions.eq("id", ideaId));

        return Optional.fromNullable(this.reconcileIdeaSummary(this.uniqueResult(criteria)));
    }

    public Optional<Idea> findByUserId(final Long aUserId) {
        final Criteria criteria = this.criteria()
                .add(Restrictions.idEq(aUserId));
        return Optional.fromNullable(this.reconcileIdeaSummary(this.uniqueResult(criteria)));
    }

    @SuppressWarnings("unchecked")
    public Set<Idea> findPopular(final int limit, final int page) {
        final int baseLimit = page * (limit / 2);
        final int pagedAmount = baseLimit + (limit / 2);
        
        // Holy fuck this ugly haha
        Query topPartVotes = this.currentSession()
                .createSQLQuery(
                        String.format("select i.id from idea i left outer join idea_part_vote v ON i.id = v.idea_id group by i.id LIMIT %d, %d", baseLimit, pagedAmount));
        Query topPartSuggestionVotes = this.currentSession()
                .createSQLQuery(String.format("select i.id from idea i left outer join idea_part_suggestion_vote v ON i.id = v.idea_id group by i.id LIMIT %d, %d",
                        baseLimit, pagedAmount));

        final List<BigInteger> topPartIds = topPartVotes.list();
        final List<BigInteger> topPartSuggestionIds = topPartSuggestionVotes.list();

        final Set<Long> ideaIds = new HashSet<Long>();

        for (final BigInteger result : topPartIds) {
            ideaIds.add(result.longValue());
        }

        for (final BigInteger result : topPartSuggestionIds) {
            ideaIds.add(result.longValue());
        }

        final Criteria criteria = this.criteria()
                .add(Restrictions.in("id", ideaIds));

        final Set<Idea> ideas = new LinkedHashSet<Idea>(criteria.list());
        for (final Idea idea : ideas) {
            this.reconcileIdeaSummary(idea);
        }

        return ideas;
    }
    
    public Set<Idea> findRecent(final int maxResults, final int page) {
        final int baseLimit = page == 1 ? 0 : page * maxResults;
        final int pagedAmount = baseLimit + maxResults;
        
        final Criteria criteria = this.criteria()
                .addOrder(Order.desc("createdAt"))
                .setFirstResult(baseLimit)
                .setMaxResults(pagedAmount);

        final Set<Idea> ideas = new LinkedHashSet<Idea>(criteria.list());
        for (final Idea idea : ideas) {
            this.reconcileIdeaSummary(idea);
        }

        return ideas;
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

    // TODO: This should really be moved somewhere else
    private Idea reconcileIdeaSummary(final Idea idea) {
        if (idea == null) {
            return idea;
        }

        // TODO: Move magic numbers into an enum somewhere
        if (idea.getIdeaParts().containsKey(1)) {
            idea.setName(idea.getIdeaParts().get(1).getContent());
        }

        if (idea.getIdeaParts().containsKey(2)) {
            idea.setElevatorPitch(idea.getIdeaParts().get(2).getContent());
        }

        final User user = this.userDAO.findById(idea.getUserId()).get();
        idea.setUserName(user.getName());
        idea.setAvatarUrl(user.getAvatarUrl());

        int votes = 0;
        int contributions = 0;
        if (idea.getIdeaParts() != null) {
            for (final IdeaPart ideaPart : idea.getIdeaParts().values()) {
                votes += ideaPart.getUpvotes();
                votes += ideaPart.getDownvotes();

                if (ideaPart.getIdeaPartSuggestions() != null) {
                    for (final IdeaPartSuggestion ideaPartSuggestion : ideaPart.getIdeaPartSuggestions()) {
                        votes += ideaPartSuggestion.getUpvotes();
                        votes += ideaPartSuggestion.getDownvotes();
                    }
                    contributions += ideaPart.getIdeaPartSuggestions().size();
                }
            }
        }

        idea.setVotes(votes);
        idea.setContributions(contributions);

        return idea;
    }
}
