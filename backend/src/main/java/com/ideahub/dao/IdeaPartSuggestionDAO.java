package com.ideahub.dao;
// Generated Feb 20, 2016 12:32:19 AM by Hibernate Tools 4.3.1

import java.util.List;
import javax.naming.InitialContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.LockMode;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Example;

import com.ideahub.model.IdeaPartSuggestion;

/**
 * Home object for domain model class IdeaPartSuggestion.
 * @see .IdeaPartSuggestion
 * @author Hibernate Tools
 */
public class IdeaPartSuggestionDAO {

    private static final Log log = LogFactory.getLog(IdeaPartSuggestionDAO.class);

    private final SessionFactory sessionFactory = getSessionFactory();

    protected SessionFactory getSessionFactory() {
        try {
            return (SessionFactory) new InitialContext().lookup("SessionFactory");
        } catch (Exception e) {
            log.error("Could not locate SessionFactory in JNDI", e);
            throw new IllegalStateException("Could not locate SessionFactory in JNDI");
        }
    }

    public void persist(IdeaPartSuggestion transientInstance) {
        log.debug("persisting IdeaPartSuggestion instance");
        try {
            sessionFactory.getCurrentSession().persist(transientInstance);
            log.debug("persist successful");
        } catch (RuntimeException re) {
            log.error("persist failed", re);
            throw re;
        }
    }

    public void attachDirty(IdeaPartSuggestion instance) {
        log.debug("attaching dirty IdeaPartSuggestion instance");
        try {
            sessionFactory.getCurrentSession().saveOrUpdate(instance);
            log.debug("attach successful");
        } catch (RuntimeException re) {
            log.error("attach failed", re);
            throw re;
        }
    }

    public void attachClean(IdeaPartSuggestion instance) {
        log.debug("attaching clean IdeaPartSuggestion instance");
        try {
            sessionFactory.getCurrentSession().lock(instance, LockMode.NONE);
            log.debug("attach successful");
        } catch (RuntimeException re) {
            log.error("attach failed", re);
            throw re;
        }
    }

    public void delete(IdeaPartSuggestion persistentInstance) {
        log.debug("deleting IdeaPartSuggestion instance");
        try {
            sessionFactory.getCurrentSession().delete(persistentInstance);
            log.debug("delete successful");
        } catch (RuntimeException re) {
            log.error("delete failed", re);
            throw re;
        }
    }

    public IdeaPartSuggestion merge(IdeaPartSuggestion detachedInstance) {
        log.debug("merging IdeaPartSuggestion instance");
        try {
            IdeaPartSuggestion result = (IdeaPartSuggestion) sessionFactory.getCurrentSession()
                    .merge(detachedInstance);
            log.debug("merge successful");
            return result;
        } catch (RuntimeException re) {
            log.error("merge failed", re);
            throw re;
        }
    }

    public IdeaPartSuggestion findById(int id) {
        log.debug("getting IdeaPartSuggestion instance with id: " + id);
        try {
            IdeaPartSuggestion instance = (IdeaPartSuggestion) sessionFactory.getCurrentSession()
                    .get("IdeaPartSuggestion", id);
            if (instance == null) {
                log.debug("get successful, no instance found");
            } else {
                log.debug("get successful, instance found");
            }
            return instance;
        } catch (RuntimeException re) {
            log.error("get failed", re);
            throw re;
        }
    }

    public List findByExample(IdeaPartSuggestion instance) {
        log.debug("finding IdeaPartSuggestion instance by example");
        try {
            List results = sessionFactory.getCurrentSession()
                    .createCriteria("IdeaPartSuggestion")
                    .add(Example.create(instance))
                    .list();
            log.debug("find by example successful, result size: " + results.size());
            return results;
        } catch (RuntimeException re) {
            log.error("find by example failed", re);
            throw re;
        }
    }
}
