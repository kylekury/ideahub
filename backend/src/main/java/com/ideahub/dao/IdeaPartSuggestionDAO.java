package com.ideahub.dao;

import org.hibernate.SessionFactory;

import com.ideahub.model.IdeaPartSuggestion;

import io.dropwizard.hibernate.AbstractDAO;

public class IdeaPartSuggestionDAO extends AbstractDAO<IdeaPartSuggestion> {
    public IdeaPartSuggestionDAO(final SessionFactory sessionFactory) {
        super(sessionFactory);
    }
}
