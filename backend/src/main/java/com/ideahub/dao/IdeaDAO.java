package com.ideahub.dao;

import org.hibernate.SessionFactory;

import com.ideahub.model.Idea;

import io.dropwizard.hibernate.AbstractDAO;

public class IdeaDAO extends AbstractDAO<Idea> {
    public IdeaDAO(final SessionFactory sessionFactory) {
        super(sessionFactory);
    }
}
