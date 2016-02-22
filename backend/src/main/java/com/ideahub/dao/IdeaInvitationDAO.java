package com.ideahub.dao;

import com.google.common.base.Optional;
import com.ideahub.cache.IdeaDefinitionCache;
import com.ideahub.model.IdeaInvitation;
import com.ideahub.model.IdeaPartType;
import com.ideahub.model.User;
import io.dropwizard.hibernate.AbstractDAO;
import jodd.petite.meta.PetiteBean;
import jodd.petite.meta.PetiteInject;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

import java.util.List;

@PetiteBean
public class IdeaInvitationDAO extends AbstractDAO<IdeaInvitation> {

    @PetiteInject
    public IdeaInvitationDAO(final SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public IdeaInvitation create(IdeaInvitation invitation) {
        return this.persist(invitation);
    }

    public IdeaInvitation update(final IdeaInvitation invitation) {
        final IdeaInvitation old = this.get(invitation.getId());

        old.setAcceptedState(invitation.isAcceptedState());

        return (IdeaInvitation) this.currentSession().merge(invitation);
    }
    public Optional<IdeaInvitation> findById(final int id) {
        final Criteria criteria = this.criteria()
                .add(Restrictions.eq("id", id));
        return Optional.fromNullable(this.uniqueResult(criteria));
    }
}
