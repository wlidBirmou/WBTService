/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nrz.fairhandlerservice.model;

import java.util.Observable;
import nrz.fairhandlerservice.jpa.Balancestate;
import nrz.fairhandlerservice.jpa.Decoderstate;
import nrz.fairhandlerservice.jpaController.BalancestateJpaController;
import nrz.fairhandlerservice.jpaController.DecoderstateJpaController;
import nrz.fairhandlerservice.jpaController.exceptions.IllegalOrphanException;
import nrz.fairhandlerservice.jpaController.exceptions.NonexistentEntityException;
import nrz.fairhandlerservice.jpaController.exceptions.PreexistingEntityException;

/**
 *
 * @author rahimAdmin
 */
public class TerminalsModel extends Observable{
    DecoderstateJpaController decoderstateJpaController=AbstractModel.getDecoderstateJpaController();
    BalancestateJpaController balancestateJpaController=AbstractModel.getBalancestateJpaController();

    public void createBalancestate(Balancestate balancestate) throws PreexistingEntityException, Exception {
        balancestateJpaController.create(balancestate);
    }

    public void editBalancestate(Balancestate balancestate) throws NonexistentEntityException, Exception {
        balancestateJpaController.edit(balancestate);
    }

    public void removeBalancestate(Integer id) throws NonexistentEntityException {
        balancestateJpaController.destroy(id);
    }

    public Balancestate findBalancestate(Integer id) {
        return balancestateJpaController.findBalancestate(id);
    }

    public void createDecoderstate(Decoderstate decoderstate) throws PreexistingEntityException, Exception {
        decoderstateJpaController.create(decoderstate);
    }

    public void editDecoderstate(Decoderstate decoderstate) throws IllegalOrphanException, NonexistentEntityException, Exception {
        decoderstateJpaController.edit(decoderstate);
    }

    public void removeDecoderstate(Integer id) throws IllegalOrphanException, NonexistentEntityException {
        decoderstateJpaController.destroy(id);
    }

    public Decoderstate findDecoderstate(Integer id) {
        return decoderstateJpaController.findDecoderstate(id);
    }
    
    
    
}
