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
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import nrz.fairhandlerservice.jpa.Pruduct;
import nrz.fairhandlerservice.jpaController.exceptions.NonexistentEntityException;

/**
 *
 * @author rahimAdmin
 */
public class PruductJpaController implements Serializable {

    public PruductJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Pruduct pruduct) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(pruduct);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Pruduct pruduct) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            pruduct = em.merge(pruduct);
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = pruduct.getIdArticle();
                if (findPruduct(id) == null) {
                    throw new NonexistentEntityException("The pruduct with id " + id + " no longer exists.");
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
            Pruduct pruduct;
            try {
                pruduct = em.getReference(Pruduct.class, id);
                pruduct.getIdArticle();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The pruduct with id " + id + " no longer exists.", enfe);
            }
            em.remove(pruduct);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Pruduct> findPruductEntities() {
        return findPruductEntities(true, -1, -1);
    }

    public List<Pruduct> findPruductEntities(int maxResults, int firstResult) {
        return findPruductEntities(false, maxResults, firstResult);
    }

    private List<Pruduct> findPruductEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Pruduct.class));
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

    public Pruduct findPruduct(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Pruduct.class, id);
        } finally {
            em.close();
        }
    }

    public int getPruductCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Pruduct> rt = cq.from(Pruduct.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
     public Object findPruductWithCode(String codeArticle) {
        EntityManager em = this.getEntityManager();
        Query query = em.createQuery("SELECT  p FROM Pruduct p WHERE p.articleCode='" + codeArticle + "'");
        try {
            Object result = query.getSingleResult();
            return result;
        } catch (NoResultException|NonUniqueResultException ex) {
            return -1;
        }
    }
     
     public boolean isProductByCodeExist(String articleCode) {
        EntityManager em = this.getEntityManager();
        Query query = em.createQuery("SELECT p FROM Pruduct p WHERE p.articleCode='" + articleCode+"'");
        List list = query.getResultList();
        return !list.isEmpty();
    } 
}
