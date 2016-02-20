package com.ideahub.dao;

import java.util.List;

import org.hibernate.SessionFactory;

import com.ideahub.model.IdeaPartType;

import io.dropwizard.hibernate.AbstractDAO;
import jodd.petite.meta.PetiteBean;
import jodd.petite.meta.PetiteInject;

@PetiteBean
public class IdeaPartTypeDAO extends AbstractDAO<IdeaPartType> {
    @PetiteInject
    public IdeaPartTypeDAO(final SessionFactory sessionFactory) {
        super(sessionFactory);
    }
    
    public List<IdeaPartType> getIdeaDefinition() {
        return this.list(this.criteria());
    }
}
