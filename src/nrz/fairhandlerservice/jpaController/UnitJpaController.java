/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nrz.fairhandlerservice.jpaController;

import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import nrz.fairhandlerservice.jpa.Unit;
import nrz.fairhandlerservice.jpaController.exceptions.NonexistentEntityException;

/**
 *
 * @author rahimAdmin
 */
public class UnitJpaController implements Serializable {

    public UnitJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Unit unit) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(unit);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Unit unit) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            unit = em.merge(unit);
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = unit.getIdunite();
                if (findUnit(id) == null) {
                    throw new NonexistentEntityException("The unit with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Unit unit;
            try {
                unit = em.getReference(Unit.class, id);
                unit.getIdunite();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The unit with id " + id + " no longer exists.", enfe);
            }
            em.remove(unit);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Unit> findUnitEntities() {
        return findUnitEntities(true, -1, -1);
    }

    public List<Unit> findUnitEntities(int maxResults, int firstResult) {
        return findUnitEntities(false, maxResults, firstResult);
    }

    private List<Unit> findUnitEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Unit.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Unit findUnit(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Unit.class, id);
        } finally {
            em.close();
        }
    }

    public int getUnitCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Unit> rt = cq.from(Unit.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
     public boolean isUnitByAbreviationExist(String abreviation) {
        EntityManager em = this.getEntityManager();
        Query query = em.createQuery("SELECT u FROM Unit u WHERE u.abrevation='" + abreviation + "'");
        List list = query.getResultList();
        if (list.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    public Unit getUnitByAbreviation(String abreviation) {
        EntityManager em = this.getEntityManager();
        Query query = em.createQuery("SELECT u FROM Unit u WHERE u.abrevation='" + abreviation + "'");
        return (Unit) query.getSingleResult();
    }
    
}
