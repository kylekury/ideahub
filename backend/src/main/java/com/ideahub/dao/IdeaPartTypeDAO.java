package com.ideahub.dao;

import org.hibernate.SessionFactory;

import com.ideahub.model.IdeaPartType;

import io.dropwizard.hibernate.AbstractDAO;

public class IdeaPartTypeDAO extends AbstractDAO<IdeaPartType> {
    public IdeaPartTypeDAO(final SessionFactory sessionFactory) {
        super(sessionFactory);
    }
}
