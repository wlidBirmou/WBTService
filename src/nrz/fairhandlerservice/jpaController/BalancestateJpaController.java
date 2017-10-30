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
import nrz.fairhandlerservice.jpa.Balancestate;
import nrz.fairhandlerservice.jpa.Decoderstate;
import nrz.fairhandlerservice.jpaController.exceptions.NonexistentEntityException;

/**
 *
 * @author rahimAdmin
 */
public class BalancestateJpaController implements Serializable {

    public BalancestateJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Balancestate balancestate) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Decoderstate decoderState = balancestate.getDecoderState();
            if (decoderState != null) {
                decoderState = em.getReference(decoderState.getClass(), decoderState.getIdDecoderState());
                balancestate.setDecoderState(decoderState);
            }
            em.persist(balancestate);
            if (decoderState != null) {
                decoderState.getBalancestateCollection().add(balancestate);
                decoderState = em.merge(decoderState);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Balancestate balancestate) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Balancestate persistentBalancestate = em.find(Balancestate.class, balancestate.getIdbalanceState());
            Decoderstate decoderStateOld = persistentBalancestate.getDecoderState();
            Decoderstate decoderStateNew = balancestate.getDecoderState();
            if (decoderStateNew != null) {
                decoderStateNew = em.getReference(decoderStateNew.getClass(), decoderStateNew.getIdDecoderState());
                balancestate.setDecoderState(decoderStateNew);
            }
            balancestate = em.merge(balancestate);
            if (decoderStateOld != null && !decoderStateOld.equals(decoderStateNew)) {
                decoderStateOld.getBalancestateCollection().remove(balancestate);
                decoderStateOld = em.merge(decoderStateOld);
            }
            if (decoderStateNew != null && !decoderStateNew.equals(decoderStateOld)) {
                decoderStateNew.getBalancestateCollection().add(balancestate);
                decoderStateNew = em.merge(decoderStateNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = balancestate.getIdbalanceState();
                if (findBalancestate(id) == null) {
                    throw new NonexistentEntityException("The balancestate with id " + id + " no longer exists.");
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
            Balancestate balancestate;
            try {
                balancestate = em.getReference(Balancestate.class, id);
                balancestate.getIdbalanceState();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The balancestate with id " + id + " no longer exists.", enfe);
            }
            Decoderstate decoderState = balancestate.getDecoderState();
            if (decoderState != null) {
                decoderState.getBalancestateCollection().remove(balancestate);
                decoderState = em.merge(decoderState);
            }
            em.remove(balancestate);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Balancestate> findBalancestateEntities() {
        return findBalancestateEntities(true, -1, -1);
    }

    public List<Balancestate> findBalancestateEntities(int maxResults, int firstResult) {
        return findBalancestateEntities(false, maxResults, firstResult);
    }

    private List<Balancestate> findBalancestateEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Balancestate.class));
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

    public Balancestate findBalancestate(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Balancestate.class, id);
        } finally {
            em.close();
        }
    }

    public int getBalancestateCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Balancestate> rt = cq.from(Balancestate.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
