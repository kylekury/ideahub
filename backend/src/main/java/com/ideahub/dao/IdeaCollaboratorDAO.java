package com.ideahub.dao;
// Generated Feb 20, 2016 12:32:19 AM by Hibernate Tools 4.3.1

import com.google.common.base.Optional;
import com.ideahub.model.IdeaCollaborator;
import com.ideahub.model.IdeaCollaboratorId;
import io.dropwizard.hibernate.AbstractDAO;
import jodd.petite.meta.PetiteInject;
import org.hibernate.SessionFactory;

/**
 * Home object for domain model class IdeaCollaborator.
 *
 * @author Hibernate Tools
 * @see .IdeaCollaborator
 */
public class IdeaCollaboratorDAO extends AbstractDAO<IdeaCollaborator> {
    @PetiteInject
    public IdeaCollaboratorDAO(final SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public Optional<IdeaCollaborator> findById(Long aUserId, Long anIdeaId) {
        IdeaCollaboratorId key = IdeaCollaboratorId.builder()
                .ideaId(anIdeaId.intValue())
                .userId(aUserId.intValue())
                .build();
        return Optional.fromNullable(this.get(key));
    }
}
