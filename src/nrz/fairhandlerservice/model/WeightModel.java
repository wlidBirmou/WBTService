/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nrz.fairhandlerservice.model;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Observable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import nrz.fairHandlerStates.files.path.SoftFiles;
import nrz.fairhandlerservice.jpa.Pruduct;
import nrz.fairhandlerservice.jpa.Unit;
import nrz.fairhandlerservice.jpa.Weight;
import nrz.fairhandlerservice.jpaController.PruductJpaController;
import nrz.fairhandlerservice.jpaController.UnitJpaController;
import nrz.fairhandlerservice.jpaController.WeightJpaController;
import nrz.fairhandlerservice.jpaController.exceptions.NonexistentEntityException;

/**
 *
 * @author rahimAdmin
 */
public class WeightModel extends Observable {

    private static final WeightJpaController weightJpaController = AbstractModel.getWeightJpaController();
    private static final UnitJpaController unitJpaController = AbstractModel.getUnitJpaController();

    private HashMap<String, Integer> rcdCodeIdHashMap;
    private ObjectOutputStream rcdOutputStream;
    private ObjectInputStream rcdInputStream;

    public WeightModel() {
        this.rcdCodeIdHashMap = new HashMap<String, Integer>();
        this.loadRcdCode();
    
    }

    public void createWeight(Weight weight) {
        weightJpaController.create(weight);
    }

    public void editWeight(Weight weight) throws NonexistentEntityException, Exception {
        weightJpaController.edit(weight);
    }

    public Weight findWeight(Integer id) {
        return weightJpaController.findWeight(id);
    }

   

    public void createUnit(Unit unit) {
        unitJpaController.create(unit);
    }

    

   

    public Weight getWeight(String rCDCode) {
        if (rcdCodeIdHashMap.containsKey(rCDCode)) {
            Weight weight = this.findWeight(rcdCodeIdHashMap.get(rCDCode));
            this.rcdCodeIdHashMap.remove(rCDCode);
            return weight;
        } else {

            return new Weight();
        }
    }

    public boolean mapContainRCd(String key) {
        return rcdCodeIdHashMap.containsKey(key);
    }

    public boolean mapContainsId(int value) {
        return rcdCodeIdHashMap.containsValue(value);
    }

    private void recordRcdCode() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    rcdOutputStream = new ObjectOutputStream(new FileOutputStream(SoftFiles.RCD_FILE_PATH.getValue().toFile()));
                    try {
                        rcdOutputStream.writeObject(rcdCodeIdHashMap);
                        rcdOutputStream.flush();
                    } catch (IOException ex) {
                        Logger.getLogger(WeightModel.class.getName()).log(Level.SEVERE, null, ex);
                    }

                } catch (FileNotFoundException ex) {
                    Logger.getLogger(WeightModel.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(WeightModel.class.getName()).log(Level.SEVERE, null, ex);
                } finally {
                    if (rcdOutputStream != null) {
                        try {
                            rcdOutputStream.close();
                        } catch (IOException ex) {
                            Logger.getLogger(WeightModel.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }

                executorService.shutdown();

            }
        }
        );
    }

    private void loadRcdCode() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    rcdInputStream = new ObjectInputStream(new FileInputStream(SoftFiles.RCD_FILE_PATH.getValue().toFile()));
                    try {
                        rcdCodeIdHashMap = (HashMap<String, Integer>) rcdInputStream.readObject();
                    } catch (IOException ex) {
                        Logger.getLogger(WeightModel.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (ClassNotFoundException ex) {
                        Logger.getLogger(WeightModel.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(WeightModel.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(WeightModel.class.getName()).log(Level.SEVERE, null, ex);
                } finally {
                    if (rcdInputStream != null) {
                        try {
                            rcdInputStream.close();
                        } catch (IOException ex) {
                            Logger.getLogger(WeightModel.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
                executorService.shutdown();
            }
        });
    }

    public void addRCDIdToBuffer(String rCDCode, int idWeight) {
        this.rcdCodeIdHashMap.put(rCDCode, idWeight);
        this.recordRcdCode();
        
    }

    public int getLastWeightId() {
        return this.weightJpaController.getLastId();
    }

    
    public boolean isUnitByAbreviationExist(String abreviation) {
        return unitJpaController.isUnitByAbreviationExist(abreviation);
    }

    public Unit getUnitByAbreviation(String abreviation) {
        return unitJpaController.getUnitByAbreviation(abreviation);
    }
}
