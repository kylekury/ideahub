package com.ideahub.dao;
// Generated Feb 20, 2016 12:32:19 AM by Hibernate Tools 4.3.1

import java.util.List;
import javax.naming.InitialContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.LockMode;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Example;

import com.ideahub.model.IdeaPartType;

/**
 * Home object for domain model class IdeaPartType.
 * @see .IdeaPartType
 * @author Hibernate Tools
 */
public class IdeaPartTypeDAO {

    private static final Log log = LogFactory.getLog(IdeaPartTypeDAO.class);

    private final SessionFactory sessionFactory = getSessionFactory();

    protected SessionFactory getSessionFactory() {
        try {
            return (SessionFactory) new InitialContext().lookup("SessionFactory");
        } catch (Exception e) {
            log.error("Could not locate SessionFactory in JNDI", e);
            throw new IllegalStateException("Could not locate SessionFactory in JNDI");
        }
    }

    public void persist(IdeaPartType transientInstance) {
        log.debug("persisting IdeaPartType instance");
        try {
            sessionFactory.getCurrentSession().persist(transientInstance);
            log.debug("persist successful");
        } catch (RuntimeException re) {
            log.error("persist failed", re);
            throw re;
        }
    }

    public void attachDirty(IdeaPartType instance) {
        log.debug("attaching dirty IdeaPartType instance");
        try {
            sessionFactory.getCurrentSession().saveOrUpdate(instance);
            log.debug("attach successful");
        } catch (RuntimeException re) {
            log.error("attach failed", re);
            throw re;
        }
    }

    public void attachClean(IdeaPartType instance) {
        log.debug("attaching clean IdeaPartType instance");
        try {
            sessionFactory.getCurrentSession().lock(instance, LockMode.NONE);
            log.debug("attach successful");
        } catch (RuntimeException re) {
            log.error("attach failed", re);
            throw re;
        }
    }

    public void delete(IdeaPartType persistentInstance) {
        log.debug("deleting IdeaPartType instance");
        try {
            sessionFactory.getCurrentSession().delete(persistentInstance);
            log.debug("delete successful");
        } catch (RuntimeException re) {
            log.error("delete failed", re);
            throw re;
        }
    }

    public IdeaPartType merge(IdeaPartType detachedInstance) {
        log.debug("merging IdeaPartType instance");
        try {
            IdeaPartType result = (IdeaPartType) sessionFactory.getCurrentSession()
                    .merge(detachedInstance);
            log.debug("merge successful");
            return result;
        } catch (RuntimeException re) {
            log.error("merge failed", re);
            throw re;
        }
    }

    public IdeaPartType findById(int id) {
        log.debug("getting IdeaPartType instance with id: " + id);
        try {
            IdeaPartType instance = (IdeaPartType) sessionFactory.getCurrentSession()
                    .get("IdeaPartType", id);
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

    public List findByExample(IdeaPartType instance) {
        log.debug("finding IdeaPartType instance by example");
        try {
            List results = sessionFactory.getCurrentSession()
                    .createCriteria("IdeaPartType")
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
