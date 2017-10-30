/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nrz.fairhandlerservice.jpaController;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import nrz.fairhandlerservice.jpa.Balancestate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import nrz.fairhandlerservice.jpa.Decoderstate;
import nrz.fairhandlerservice.jpaController.exceptions.IllegalOrphanException;
import nrz.fairhandlerservice.jpaController.exceptions.NonexistentEntityException;

/**
 *
 * @author rahimAdmin
 */
public class DecoderstateJpaController implements Serializable {

    public DecoderstateJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Decoderstate decoderstate) {
        if (decoderstate.getBalancestateCollection() == null) {
            decoderstate.setBalancestateCollection(new ArrayList<Balancestate>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Collection<Balancestate> attachedBalancestateCollection = new ArrayList<Balancestate>();
            for (Balancestate balancestateCollectionBalancestateToAttach : decoderstate.getBalancestateCollection()) {
                balancestateCollectionBalancestateToAttach = em.getReference(balancestateCollectionBalancestateToAttach.getClass(), balancestateCollectionBalancestateToAttach.getIdbalanceState());
                attachedBalancestateCollection.add(balancestateCollectionBalancestateToAttach);
            }
            decoderstate.setBalancestateCollection(attachedBalancestateCollection);
            em.persist(decoderstate);
            for (Balancestate balancestateCollectionBalancestate : decoderstate.getBalancestateCollection()) {
                Decoderstate oldDecoderStateOfBalancestateCollectionBalancestate = balancestateCollectionBalancestate.getDecoderState();
                balancestateCollectionBalancestate.setDecoderState(decoderstate);
                balancestateCollectionBalancestate = em.merge(balancestateCollectionBalancestate);
                if (oldDecoderStateOfBalancestateCollectionBalancestate != null) {
                    oldDecoderStateOfBalancestateCollectionBalancestate.getBalancestateCollection().remove(balancestateCollectionBalancestate);
                    oldDecoderStateOfBalancestateCollectionBalancestate = em.merge(oldDecoderStateOfBalancestateCollectionBalancestate);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Decoderstate decoderstate) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Decoderstate persistentDecoderstate = em.find(Decoderstate.class, decoderstate.getIdDecoderState());
            Collection<Balancestate> balancestateCollectionOld = persistentDecoderstate.getBalancestateCollection();
            Collection<Balancestate> balancestateCollectionNew = decoderstate.getBalancestateCollection();
            List<String> illegalOrphanMessages = null;
            for (Balancestate balancestateCollectionOldBalancestate : balancestateCollectionOld) {
                if (!balancestateCollectionNew.contains(balancestateCollectionOldBalancestate)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Balancestate " + balancestateCollectionOldBalancestate + " since its decoderState field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Collection<Balancestate> attachedBalancestateCollectionNew = new ArrayList<Balancestate>();
            for (Balancestate balancestateCollectionNewBalancestateToAttach : balancestateCollectionNew) {
                balancestateCollectionNewBalancestateToAttach = em.getReference(balancestateCollectionNewBalancestateToAttach.getClass(), balancestateCollectionNewBalancestateToAttach.getIdbalanceState());
                attachedBalancestateCollectionNew.add(balancestateCollectionNewBalancestateToAttach);
            }
            balancestateCollectionNew = attachedBalancestateCollectionNew;
            decoderstate.setBalancestateCollection(balancestateCollectionNew);
            decoderstate = em.merge(decoderstate);
            for (Balancestate balancestateCollectionNewBalancestate : balancestateCollectionNew) {
                if (!balancestateCollectionOld.contains(balancestateCollectionNewBalancestate)) {
                    Decoderstate oldDecoderStateOfBalancestateCollectionNewBalancestate = balancestateCollectionNewBalancestate.getDecoderState();
                    balancestateCollectionNewBalancestate.setDecoderState(decoderstate);
                    balancestateCollectionNewBalancestate = em.merge(balancestateCollectionNewBalancestate);
                    if (oldDecoderStateOfBalancestateCollectionNewBalancestate != null && !oldDecoderStateOfBalancestateCollectionNewBalancestate.equals(decoderstate)) {
                        oldDecoderStateOfBalancestateCollectionNewBalancestate.getBalancestateCollection().remove(balancestateCollectionNewBalancestate);
                        oldDecoderStateOfBalancestateCollectionNewBalancestate = em.merge(oldDecoderStateOfBalancestateCollectionNewBalancestate);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = decoderstate.getIdDecoderState();
                if (findDecoderstate(id) == null) {
                    throw new NonexistentEntityException("The decoderstate with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Decoderstate decoderstate;
            try {
                decoderstate = em.getReference(Decoderstate.class, id);
                decoderstate.getIdDecoderState();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The decoderstate with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Balancestate> balancestateCollectionOrphanCheck = decoderstate.getBalancestateCollection();
            for (Balancestate balancestateCollectionOrphanCheckBalancestate : balancestateCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Decoderstate (" + decoderstate + ") cannot be destroyed since the Balancestate " + balancestateCollectionOrphanCheckBalancestate + " in its balancestateCollection field has a non-nullable decoderState field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(decoderstate);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Decoderstate> findDecoderstateEntities() {
        return findDecoderstateEntities(true, -1, -1);
    }

    public List<Decoderstate> findDecoderstateEntities(int maxResults, int firstResult) {
        return findDecoderstateEntities(false, maxResults, firstResult);
    }

    private List<Decoderstate> findDecoderstateEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Decoderstate.class));
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

    public Decoderstate findDecoderstate(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Decoderstate.class, id);
        } finally {
            em.close();
        }
    }

    public int getDecoderstateCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Decoderstate> rt = cq.from(Decoderstate.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
