package com.ideahub.dao;
// Generated Feb 20, 2016 12:32:19 AM by Hibernate Tools 4.3.1

import java.util.List;
import javax.naming.InitialContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.LockMode;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Example;

import com.ideahub.model.IdeaCollaborator;
import com.ideahub.model.IdeaCollaboratorId;

/**
 * Home object for domain model class IdeaCollaborator.
 * @see .IdeaCollaborator
 * @author Hibernate Tools
 */
public class IdeaCollaboratorDAO {

    private static final Log log = LogFactory.getLog(IdeaCollaboratorDAO.class);

    private final SessionFactory sessionFactory = getSessionFactory();

    protected SessionFactory getSessionFactory() {
        try {
            return (SessionFactory) new InitialContext().lookup("SessionFactory");
        } catch (Exception e) {
            log.error("Could not locate SessionFactory in JNDI", e);
            throw new IllegalStateException("Could not locate SessionFactory in JNDI");
        }
    }

    public void persist(IdeaCollaborator transientInstance) {
        log.debug("persisting IdeaCollaborator instance");
        try {
            sessionFactory.getCurrentSession().persist(transientInstance);
            log.debug("persist successful");
        } catch (RuntimeException re) {
            log.error("persist failed", re);
            throw re;
        }
    }

    public void attachDirty(IdeaCollaborator instance) {
        log.debug("attaching dirty IdeaCollaborator instance");
        try {
            sessionFactory.getCurrentSession().saveOrUpdate(instance);
            log.debug("attach successful");
        } catch (RuntimeException re) {
            log.error("attach failed", re);
            throw re;
        }
    }

    public void attachClean(IdeaCollaborator instance) {
        log.debug("attaching clean IdeaCollaborator instance");
        try {
            sessionFactory.getCurrentSession().lock(instance, LockMode.NONE);
            log.debug("attach successful");
        } catch (RuntimeException re) {
            log.error("attach failed", re);
            throw re;
        }
    }

    public void delete(IdeaCollaborator persistentInstance) {
        log.debug("deleting IdeaCollaborator instance");
        try {
            sessionFactory.getCurrentSession().delete(persistentInstance);
            log.debug("delete successful");
        } catch (RuntimeException re) {
            log.error("delete failed", re);
            throw re;
        }
    }

    public IdeaCollaborator merge(IdeaCollaborator detachedInstance) {
        log.debug("merging IdeaCollaborator instance");
        try {
            IdeaCollaborator result = (IdeaCollaborator) sessionFactory.getCurrentSession()
                    .merge(detachedInstance);
            log.debug("merge successful");
            return result;
        } catch (RuntimeException re) {
            log.error("merge failed", re);
            throw re;
        }
    }

    public IdeaCollaborator findById(IdeaCollaboratorId id) {
        log.debug("getting IdeaCollaborator instance with id: " + id);
        try {
            IdeaCollaborator instance = (IdeaCollaborator) sessionFactory.getCurrentSession()
                    .get("IdeaCollaborator", id);
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

    public List findByExample(IdeaCollaborator instance) {
        log.debug("finding IdeaCollaborator instance by example");
        try {
            List results = sessionFactory.getCurrentSession()
                    .createCriteria("IdeaCollaborator")
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
