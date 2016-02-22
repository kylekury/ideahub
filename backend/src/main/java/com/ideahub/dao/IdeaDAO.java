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

import io.dropwizard.hibernate.AbstractDAO;
import jodd.petite.meta.PetiteBean;
import jodd.petite.meta.PetiteInject;

@PetiteBean
public class IdeaDAO extends AbstractDAO<Idea> {
    // TODO: This naughty and bad
    private UserDAO userDAO;

    @PetiteInject
    public IdeaDAO(final SessionFactory sessionFactory, UserDAO userDAO) {
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

        return reconcileIdeaSummary((Idea) this.currentSession().merge(idea));
    }

    public Optional<Idea> findById(final long ideaId) {
        final Criteria criteria = this.criteria()
                .add(Restrictions.eq("id", ideaId));

        return Optional.fromNullable(reconcileIdeaSummary(this.uniqueResult(criteria)));
    }

    public Optional<Idea> findByUserId(final Long aUserId) {
        final Criteria criteria = this.criteria()
                .add(Restrictions.idEq(aUserId));
        return Optional.fromNullable(reconcileIdeaSummary(this.uniqueResult(criteria)));
    }

    // TODO: This should be cached, since this is called on the homepage, and doesn't need to 100% accurate
    @SuppressWarnings("unchecked")
    public List<Idea> findPopularIdeas(int limit) {
        // Holy fuck this ugly haha
        Query topPartVotes = this.currentSession()
                .createSQLQuery(
                        String.format("select i.id from idea i left outer join idea_part_vote v ON i.id = v.idea_id group by i.id LIMIT %d", limit / 2));
        Query topPartSuggestionVotes = this.currentSession()
                .createSQLQuery(String.format("select i.id from idea i left outer join idea_part_suggestion_vote v ON i.id = v.idea_id group by i.id LIMIT %d",
                        limit / 2));

        List<BigInteger> topPartIds = topPartVotes.list();
        List<BigInteger> topPartSuggestionIds = topPartSuggestionVotes.list();

        Set<Long> ideaIds = new HashSet<Long>();

        for (BigInteger result : topPartIds) {
            ideaIds.add(result.longValue());
        }

        for (BigInteger result : topPartSuggestionIds) {
            ideaIds.add(result.longValue());
        }

        Criteria criteria = this.criteria()
                .add(Restrictions.in("id", ideaIds));

        Set<Idea> ideas = new LinkedHashSet<Idea>((List<Idea>) criteria.list());
        List<Idea> exportedIdeas = new ArrayList<>();
        for (Idea idea : ideas) {
            exportedIdeas.add(reconcileIdeaSummary(idea));
        }

        return exportedIdeas;
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
    private Idea reconcileIdeaSummary(Idea idea) {
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

        idea.setUserName(userDAO.findById(idea.getUserId()).get().getName());

        int votes = 0;
        int contributions = 0;
        if (idea.getIdeaParts() != null) {
            for (IdeaPart ideaPart : idea.getIdeaParts().values()) {
                votes += ideaPart.getUpvotes();
                votes += ideaPart.getDownvotes();

                if (ideaPart.getIdeaPartSuggestions() != null) {
                    for (IdeaPartSuggestion ideaPartSuggestion : ideaPart.getIdeaPartSuggestions()) {
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

    public List<Idea> findRecent(final int maxResults) {
        final Criteria criteria = this.criteria()
                .addOrder(Order.desc("createdAt"))
                .setMaxResults(maxResults);

        List<Idea> ideas = this.list(criteria);
        for (Idea idea : ideas) {
            reconcileIdeaSummary(idea);
        }

        return ideas;
    }
}
