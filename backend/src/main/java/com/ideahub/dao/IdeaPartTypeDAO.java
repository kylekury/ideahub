package com.ideahub.dao;

import java.util.List;

import com.google.common.base.Optional;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;

import com.ideahub.model.IdeaPartType;

import io.dropwizard.hibernate.AbstractDAO;
import jodd.petite.meta.PetiteBean;
import jodd.petite.meta.PetiteInject;
import org.hibernate.criterion.Restrictions;

@PetiteBean
public class IdeaPartTypeDAO extends AbstractDAO<IdeaPartType> {
    @PetiteInject
    public IdeaPartTypeDAO(final SessionFactory sessionFactory) {
        super(sessionFactory);
    }
    
    public List<IdeaPartType> getIdeaDefinition() {
        return this.list(this.criteria());
    }

    public Optional<IdeaPartType> findByName(String aTypeName) {
        final Criteria criteria = this.criteria()
                .add(Restrictions.eq("name", aTypeName));
        return Optional.fromNullable(this.uniqueResult(criteria));
    }
}
