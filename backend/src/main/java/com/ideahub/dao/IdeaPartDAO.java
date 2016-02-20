package com.ideahub.dao;

import org.hibernate.SessionFactory;

import com.ideahub.model.IdeaPart;

import io.dropwizard.hibernate.AbstractDAO;

public class IdeaPartDAO extends AbstractDAO<IdeaPart> {
    public IdeaPartDAO(final SessionFactory sessionFactory) {
        super(sessionFactory);
    }
}
