/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nrz.fairhandlerservice.model;

import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import nrz.fairhandlerservice.jpaController.BalancestateJpaController;
import nrz.fairhandlerservice.jpaController.DecoderstateJpaController;
import nrz.fairhandlerservice.jpaController.PruductJpaController;
import nrz.fairhandlerservice.jpaController.UnitJpaController;
import nrz.fairhandlerservice.jpaController.WeightJpaController;
import nrz.java.persistance.mysql.connection.MysqlInformationSchema;
import nrz.java.persistance.mysql.connection.MysqlServerInstance;


/**
 *
 * @author rahimAdmin
 */
public class AbstractModel {

    private static EntityManagerFactory emf;
    private static PruductJpaController pruductJpaController;
    private static WeightJpaController weightJpaController;
    private static UnitJpaController unitJpaController;
    private static DecoderstateJpaController decoderstateJpaController;
    private static BalancestateJpaController balancestateJpaController;

    public static PruductJpaController getPruductJpaController() {
        return pruductJpaController;
    }

    public static void setPruductJpaController(PruductJpaController pruductJpaController) {
        AbstractModel.pruductJpaController = pruductJpaController;
    }

    public static WeightJpaController getWeightJpaController() {
        return weightJpaController;
    }

    public static void setWeightJpaController(WeightJpaController weightJpaController) {
        AbstractModel.weightJpaController = weightJpaController;
    }

    public static UnitJpaController getUnitJpaController() {
        return unitJpaController;
    }

    public static void setUnitJpaController(UnitJpaController unitJpaController) {
        AbstractModel.unitJpaController = unitJpaController;
    }

    public static DecoderstateJpaController getDecoderstateJpaController() {
        return decoderstateJpaController;
    }

    public static void setDecoderstateJpaController(DecoderstateJpaController decoderstateJpaController) {
        AbstractModel.decoderstateJpaController = decoderstateJpaController;
    }

    public static BalancestateJpaController getBalancestateJpaController() {
        return balancestateJpaController;
    }

    public static void setBalancestateJpaController(BalancestateJpaController balancestateJpaController) {
        AbstractModel.balancestateJpaController = balancestateJpaController;
    }

    public static void initEntityManager() {
        if (AbstractModel.emf != null) {
            AbstractModel.emf = null;
        }
        MysqlInformationSchema mysqlInformationSchema = new MysqlInformationSchema("localhost", "3306", "root", "we3lehTakoulTefah");
        try {
            mysqlInformationSchema.Connect();
            if (!mysqlInformationSchema.isSchemaExist("fairhandlerdb")) {
                MysqlServerInstance mysqlServerInstance = new MysqlServerInstance("localhost", "3306", "root", "we3lehTakoulTefah");
                mysqlServerInstance.connect();
                mysqlServerInstance.createSchema("fairhandlerdb");
                mysqlServerInstance.shutDownConnection();
            }

            mysqlInformationSchema.shutDownConnection();

        } catch (SQLException ex) {
            Logger.getLogger(AbstractModel.class.getName()).log(Level.SEVERE, null, ex);
        }

        Properties properties = new Properties();
        properties.put("javax.persistence.jdbc.user", "root");
        properties.put("javax.persistence.jdbc.password", "we3lehTakoulTefah");
        AbstractModel.emf = Persistence.createEntityManagerFactory("fairHandlerPU", properties);
        AbstractModel.pruductJpaController = new PruductJpaController(emf);
        AbstractModel.weightJpaController = new WeightJpaController(emf);
        AbstractModel.unitJpaController = new UnitJpaController(emf);
        AbstractModel.decoderstateJpaController = new DecoderstateJpaController(emf);
        AbstractModel.balancestateJpaController = new BalancestateJpaController(emf);
    }
}
