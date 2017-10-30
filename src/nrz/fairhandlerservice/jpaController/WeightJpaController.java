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
import nrz.fairhandlerservice.jpa.Weight;
import nrz.fairhandlerservice.jpaController.exceptions.NonexistentEntityException;

/**
 *
 * @author rahimAdmin
 */
public class WeightJpaController implements Serializable {

    public WeightJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Weight weight) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(weight);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Weight weight) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            weight = em.merge(weight);
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = weight.getIdWeight();
                if (findWeight(id) == null) {
                    throw new NonexistentEntityException("The weight with id " + id + " no longer exists.");
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
            Weight weight;
            try {
                weight = em.getReference(Weight.class, id);
                weight.getIdWeight();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The weight with id " + id + " no longer exists.", enfe);
            }
            em.remove(weight);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Weight> findWeightEntities() {
        return findWeightEntities(true, -1, -1);
    }

    public List<Weight> findWeightEntities(int maxResults, int firstResult) {
        return findWeightEntities(false, maxResults, firstResult);
    }

    private List<Weight> findWeightEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Weight.class));
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

    public Weight findWeight(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Weight.class, id);
        } finally {
            em.close();
        }
    }

    public int getWeightCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Weight> rt = cq.from(Weight.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
       
     public int getLastId() {
        EntityManager em = this.getEntityManager();
        Query query = em.createQuery("SELECT MAX(w.idWeight) FROM Weight w");
        return (int) query.getSingleResult();

    }
}
