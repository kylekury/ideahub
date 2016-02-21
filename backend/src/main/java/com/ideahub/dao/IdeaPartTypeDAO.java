package com.ideahub.dao;

import java.util.List;
import java.util.Map;

import com.google.common.base.Optional;
import com.ideahub.cache.IdeaDefinitionCache;
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
        IdeaDefinitionCache cache = new IdeaDefinitionCache();
        cache.setIdeaPartTypeDAO(this);
        IdeaPartType result = null;
        result = cache.getIdeaPartTypesByName().get(aTypeName);
        return Optional.fromNullable(result);
    }
}
