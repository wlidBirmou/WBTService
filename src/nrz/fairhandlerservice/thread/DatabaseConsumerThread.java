/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nrz.fairhandlerservice.thread;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import nrz.bframe.frame.BFrame;
import nrz.bframe.frameTocken.defaultFrameTocken.BitSetFrameTocken;
import nrz.fairHandlerStates.bilanciai.ev2000Frame.Ev2000Frame;
import nrz.fairhandlerservice.jpa.Weight;
import nrz.fairhandlerservice.model.WeightModel;
import nrz.fairHandlerStates.keys.FrameTockenKeys;
import nrz.fairhandlerservice.model.ProductModel;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

/**
 *
 * @author rahimAdmin
 */
public class DatabaseConsumerThread implements Runnable {

    private final LinkedBlockingQueue<BFrame> databaseBlockingQueue;
    private final WeightModel weightModel;
    private final ProductModel productModel;

    public DatabaseConsumerThread(LinkedBlockingQueue<BFrame> databaseBlockingQueue) {
        this.databaseBlockingQueue = databaseBlockingQueue;
        this.weightModel = new WeightModel();
        this.productModel = new ProductModel();

    }

    @Override
    public void run() {
        Weight weight = null;
        Ev2000Frame ev2000Frame = null;

        while (true) {
            try {
                ev2000Frame = (Ev2000Frame) databaseBlockingQueue.take();
                System.out.println("Database reached");
            } catch (InterruptedException ex) {
                Logger.getLogger(DatabaseConsumerThread.class.getName()).log(Level.SEVERE, null, ex);
            }

            if (ev2000Frame.isPredeterminedTare() && ev2000Frame.getStringTocken(FrameTockenKeys.RCD_CODE_KEY).equals("0000")) {
                weight = new Weight();
                try {
                    weight.setArticle(productModel.findPruductWithCode(ev2000Frame.getStringTocken(FrameTockenKeys.PRODUCT_CODE_KEY)));
                } catch (NullPointerException ex) {
                    weight.setArticle(productModel.findPruductWithCode("0000"));
                }
                weight.setGrossWeight(ev2000Frame.getGrossWeight());
                weight.setNetWeight(ev2000Frame.getNetWeight());
                weight.setTare(ev2000Frame.getTareWeight());
                weight.setFirstTicketDate(ev2000Frame.getDateTocken(FrameTockenKeys.DATE_KEY).toDate());
                weight.setFirstTicketTime(ev2000Frame.getTimeTocken(FrameTockenKeys.TIME_KEY).toDateTimeToday().toDate());
                weight.setType(ev2000Frame.getTypeWeight());
                weight.setGenericCode(ev2000Frame.getStringTocken(FrameTockenKeys.GENERIC_CODE_KEY));
                weight.setReference(ev2000Frame.getStringTocken(FrameTockenKeys.RCD_CODE_KEY));
                weight.setProgressiveCode(ev2000Frame.getStringTocken(FrameTockenKeys.PROGRESSIF_CODE_KEY));
                weight.setTareCode(ev2000Frame.getStringTocken(FrameTockenKeys.tare_CODE_KEY));
                BitSetFrameTocken bitSetFrameTocken = (BitSetFrameTocken) ev2000Frame.getTocken(FrameTockenKeys.REDUCED_FRAME_STATE_KEY);
                if (bitSetFrameTocken.getBitset(FrameTockenKeys.CENTER_OF_ZERO_KEY)) {
                    weight.setFirstBalanceZero(1.);
                } else {
                    weight.setFirstBalanceZero(0.);
                }
                
                weightModel.createWeight(weight);
            } else {
                weight = weightModel.getWeight(ev2000Frame.getStringTocken(FrameTockenKeys.RCD_CODE_KEY));
                if (weight.getIdWeight() == null) {
                    weight.setGrossWeight(ev2000Frame.getGrossWeight());
                    weight.setFirstTicketDate(ev2000Frame.getDateTocken(FrameTockenKeys.DATE_KEY).toDate());
                    weight.setFirstTicketTime(ev2000Frame.getTimeTocken(FrameTockenKeys.TIME_KEY).toDateTimeToday().toDate());
                    weight.setType(ev2000Frame.getTypeWeight());
                    weight.setGenericCode(ev2000Frame.getStringTocken(FrameTockenKeys.GENERIC_CODE_KEY));
                    weight.setReference(ev2000Frame.getStringTocken(FrameTockenKeys.RCD_CODE_KEY));
                    weight.setProgressiveCode(ev2000Frame.getStringTocken(FrameTockenKeys.PROGRESSIF_CODE_KEY));
                    weight.setTareCode(ev2000Frame.getStringTocken(FrameTockenKeys.tare_CODE_KEY));
                    try {
                        weight.setArticle(productModel.findPruductWithCode(ev2000Frame.getStringTocken(FrameTockenKeys.PRODUCT_CODE_KEY)));
                    } catch (NullPointerException ex) {
                        weight.setArticle(productModel.findPruductWithCode("0000"));
                    }
                    weightModel.createWeight(weight);
                    this.weightModel.addRCDIdToBuffer(weight.getReference(), weight.getIdWeight());
                } else {
                    LocalDate firstLocalDate = new LocalDate(weight.getFirstTicketDate());
                    LocalTime firstLocalTime = new LocalTime(weight.getFirstTicketTime());
                    LocalDate actualLocalDate = ev2000Frame.getDateTocken(FrameTockenKeys.DATE_KEY);
                    LocalTime actualLocalTime = ev2000Frame.getTimeTocken(FrameTockenKeys.TIME_KEY);
                    DateTime firstDateTime = new DateTime(firstLocalDate.getYear(), firstLocalDate.getMonthOfYear(), firstLocalDate.getDayOfMonth(),
                            firstLocalTime.getHourOfDay(), firstLocalTime.getMinuteOfHour(), firstLocalTime.getSecondOfMinute());
                    DateTime actualDateTime = new DateTime(actualLocalDate.getYear(), actualLocalDate.getMonthOfYear(), actualLocalDate.getDayOfMonth(),
                            actualLocalTime.getHourOfDay(), actualLocalTime.getMinuteOfHour(), actualLocalTime.getSecondOfMinute());
                    
                    ev2000Frame.completeFrame(weight.getGrossWeight());
                    weight.setGrossWeight(ev2000Frame.getGrossWeight());
                    weight.setTare(ev2000Frame.getTareWeight());
                    weight.setNetWeight(ev2000Frame.getNetWeight());

                    if (actualDateTime.isBefore(firstDateTime)) {

                        weight.setFirstTicketDate(actualLocalDate.toDate());
                        weight.setFirstTicketTime(actualLocalTime.toDateTimeToday().toDate());
                        weight.setSecondTicketDate(firstLocalDate.toDate());
                        weight.setSecondTicketTime(firstLocalTime.toDateTimeToday().toDate());
                        if (weight.getType().equals(FrameTockenKeys.POSITIVE_TYPE_KEY)) {
                            weight.setType(FrameTockenKeys.NEGATIVE_TYPE_KEY);
                        } else if (weight.getType().equals(FrameTockenKeys.NEGATIVE_TYPE_KEY)) {
                            weight.setType(FrameTockenKeys.POSITIVE_TYPE_KEY);
                        }
                    } else {

                        weight.setSecondTicketDate(actualLocalDate.toDate());
                        weight.setSecondTicketTime(actualLocalTime.toDateTimeToday().toDate());
                        weight.setType(ev2000Frame.getTypeWeight());

                    }

                    weight.setGenericCode(ev2000Frame.getStringTocken(FrameTockenKeys.GENERIC_CODE_KEY));
                    weight.setReference(ev2000Frame.getStringTocken(FrameTockenKeys.RCD_CODE_KEY));
                    weight.setProgressiveCode(ev2000Frame.getStringTocken(FrameTockenKeys.PROGRESSIF_CODE_KEY));
                    weight.setTareCode(ev2000Frame.getStringTocken(FrameTockenKeys.tare_CODE_KEY));

                    try {
                        weight.setArticle(productModel.findPruductWithCode(ev2000Frame.getStringTocken(FrameTockenKeys.PRODUCT_CODE_KEY)));
                    } catch (NullPointerException ex) {
                        weight.setArticle(productModel.findPruductWithCode("0000"));
                    }

                    try {
                        this.weightModel.editWeight(weight);
                    } catch (Exception ex) {
                        Logger.getLogger(DatabaseConsumerThread.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
                    }
                }
            }

        }
    }
}
