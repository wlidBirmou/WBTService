/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nrz.fairhandlerservice;

import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import nrz.bframe.frame.BFrame;
import nrz.fairhandlerservice.files.filesWatcher.SittingFilesDirectoryWatcher;
import nrz.fairhandlerservice.files.filesWatcher.ExecutionDirectoryWatcher;
import nrz.fairHandlerStates.model.FhsStatesModel;
import nrz.fairHandlerStates.model.GeneralSittingsModel;
import nrz.fairhandlerservice.thread.BalanceProducerThread;
import nrz.fairhandlerservice.thread.DatabaseConsumerThread;
import nrz.fairhandlerservice.thread.ParserConsumerProducerThread;
import nrz.fairhandlerservice.thread.RealTimeConsumerThread;
import nrz.fairHandlerStates.files.path.SoftDirectories;
import nrz.fairHandlerStates.keys.CommandsKeys;
import nrz.fairHandlerStates.model.FhStatesModel;
import nrz.fairhandlerservice.jpa.Pruduct;
import nrz.fairhandlerservice.jpa.Unit;
import nrz.fairhandlerservice.model.AbstractModel;
import nrz.fairhandlerservice.model.ProductModel;
import nrz.fairhandlerservice.model.WeightModel;
import org.javatuples.Pair;
import org.jdom2.JDOMException;
import org.joda.time.DateTime;

/**
 *
 * @author rahimAdmin
 */
public class FairHandlerService {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        AbstractModel.initEntityManager();
        checkDBInitialTuple();
        CommandsKeys.initCommands();
        initDirectories();
        initStatesAndConfiguration();
        initWatchers();
        initThreads();
    }

    private static void checkDBInitialTuple() {

        WeightModel weightModel = new WeightModel();
        ProductModel productModel = new ProductModel();

        if (!weightModel.isUnitByAbreviationExist("Kg")) {
            Unit unit = new Unit();
            unit.setAbrevation("Kg");
            unit.setDesignation("Kilogrammes");
            unit.setIsUniteReference((short) 1);
            weightModel.createUnit(unit);
        }

        if (!productModel.isProductByCodeExist("0000")) {
            Pruduct pruduct = new Pruduct();
            pruduct.setArticleCode("0000");
            pruduct.setDesignation("Non specifié");
            pruduct.setAddDate(DateTime.now().toDate());
            Unit unit = weightModel.getUnitByAbreviation("Kg");
            pruduct.setUnite(unit);
            productModel.createProduct(pruduct);
        }
    }

    private static void initDirectories() {

        for (SoftDirectories directories : SoftDirectories.values()) {
            Files.exists(directories.getValue());
            if (!Files.exists(directories.getValue())) {
                try {
                    Files.createDirectory(directories.getValue());
                } catch (IOException ex) {
                    Logger.getLogger(FairHandlerService.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    private static void initStatesAndConfiguration() {

        try {
            FhsStatesModel.getInstance().setActif();
            FhsStatesModel.getInstance().setOnLaunch();
        } catch (IOException ex) {
            Logger.getLogger(FairHandlerService.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            FhStatesModel.getInstance().loadStatesFile();
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(FairHandlerService.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            GeneralSittingsModel.getInstance().loadGeneralSittingDocument();
        } catch (JDOMException | IOException ex) {
            Logger.getLogger(FairHandlerService.class.getName()).log(Level.SEVERE, null, ex);
            try {
                GeneralSittingsModel.getInstance().handleCorruptedFile();
            } catch (JDOMException | IOException ex1) {
                Logger.getLogger(FairHandlerService.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }
    }

    private static void initWatchers() {

        try {
            ExecutionDirectoryWatcher executionDirectoryWatcher = new ExecutionDirectoryWatcher();
            Thread executionDirectoryThread = new Thread(executionDirectoryWatcher);
            executionDirectoryThread.start();
        } catch (IOException ex) {
            Logger.getLogger(FairHandlerService.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            SittingFilesDirectoryWatcher sittingFilesDirectoryWatcher = new SittingFilesDirectoryWatcher();
            Thread sittingThread = new Thread(sittingFilesDirectoryWatcher);
            sittingThread.start();
        } catch (IOException ex) {
            Logger.getLogger(FairHandlerService.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private static void initThreads() {

        LinkedBlockingQueue<Pair<String, Object>> BalanceBlockingQueue = new LinkedBlockingQueue(100);
        LinkedBlockingQueue<BFrame> databaseBlockingQueue = new LinkedBlockingQueue(1000);
        LinkedBlockingQueue<BFrame> realTimeBlockingQueue = new LinkedBlockingQueue(100);

        

        Thread balanceProducerThread = new Thread(new BalanceProducerThread(BalanceBlockingQueue));
        Thread parserConsumerProducerThread = new Thread(new ParserConsumerProducerThread(BalanceBlockingQueue, databaseBlockingQueue, realTimeBlockingQueue));
        Thread realTimeConsumerThread = new Thread(new RealTimeConsumerThread(realTimeBlockingQueue));
        Thread databaseConsumerThread = new Thread(new DatabaseConsumerThread(databaseBlockingQueue));

        balanceProducerThread.start();
        parserConsumerProducerThread.start();
        realTimeConsumerThread.start();
        databaseConsumerThread.start();
    }

}
