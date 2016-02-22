package com.ideahub.dao;
// Generated Feb 20, 2016 12:32:19 AM by Hibernate Tools 4.3.1

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

import com.google.common.base.Optional;
import com.ideahub.model.IdeaCollaborator;
import com.ideahub.model.IdeaCollaboratorId;

import io.dropwizard.hibernate.AbstractDAO;

import jodd.petite.meta.PetiteInject;

/**
 * Home object for domain model class IdeaCollaborator.
 *
 * @author Hibernate Tools
 * @see IdeaCollaborator
 */
public class IdeaCollaboratorDAO extends AbstractDAO<IdeaCollaborator> {
    @PetiteInject
    public IdeaCollaboratorDAO(final SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public IdeaCollaborator create(final IdeaCollaborator collaborator) {
        return this.persist(collaborator);
    }

    public Optional<IdeaCollaborator> findById(final Long aUserId, final Long anIdeaId) {
        final IdeaCollaboratorId key = IdeaCollaboratorId.builder()
                .ideaId(anIdeaId.intValue())
                .userId(aUserId.intValue())
                .build();
        return Optional.fromNullable(this.get(key));
    }

    public Optional<IdeaCollaborator> findTransactionId(final String aTransactionId) {
        final Criteria criteria = this.criteria()
                .add(Restrictions.eq("id", aTransactionId));
        return Optional.fromNullable(this.uniqueResult(criteria));
    }
}