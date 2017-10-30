/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nrz.fairhandlerservice.bilanciai.ev2000.frame.files;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import nrz.bframe.multipleFrame.MultipleFileBFrame;
import nrz.fairHandlerStates.bilanciai.ev2000Frame.DecoderStatesFrame;
import nrz.fairHandlerStates.keys.FrameTockenKeys;
import nrz.fairhandlerservice.jpa.Balancestate;
import nrz.fairhandlerservice.jpa.Decoderstate;
import nrz.fairhandlerservice.model.TerminalsModel;
import org.javatuples.Pair;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.Seconds;

/**
 *
 * @author rahimAdmin
 */
public class MultipleDecoderStatesFramesFile extends MultipleFileBFrame {

    private final LinkedBlockingQueue<Pair<Decoderstate, List<Balancestate>>> decoderStateBlockingQueue;

    private DateTime refDecoderDateTime;
    private DateTime predDecoderDateTime;
    private DateTime refBalanceDateTime;
    private String refBalanceOn;
    private Seconds periodSeconds;
    private List<Balancestate> balanceStateList;
    private ExecutorService databaseExecutorService;

    public MultipleDecoderStatesFramesFile(Path filePath, char delimiter, Seconds periodSeconds) {
        super(filePath, delimiter);
        this.decoderStateBlockingQueue = new LinkedBlockingQueue<>(1000);
        this.balanceStateList = new ArrayList<>();
        this.initDataBaseExecutorService();
        this.periodSeconds = periodSeconds;
    }

    private void initDataBaseExecutorService() {
        databaseExecutorService = Executors.newSingleThreadExecutor();
        databaseExecutorService.execute(new Runnable() {
            @Override
            public void run() {
                Decoderstate decoderstate;
                List<Balancestate> balancestatesList;
                TerminalsModel terminalsModel = new TerminalsModel();

                while (true) {
                    try {
                        Pair<Decoderstate, List<Balancestate>> pair = decoderStateBlockingQueue.take();
                        decoderstate = pair.getValue0();
                        balancestatesList = pair.getValue1();
                        terminalsModel.createDecoderstate(decoderstate);

                        for (Balancestate balancestate : balancestatesList) {
                            balancestate.setDecoderState(decoderstate);
                            terminalsModel.createBalancestate(balancestate);
                        }

                    } catch (InterruptedException ex) {
                        Logger.getLogger(MultipleDecoderStatesFramesFile.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (Exception ex) {
                        Logger.getLogger(MultipleDecoderStatesFramesFile.class.getName()).log(Level.SEVERE, null, ex);
                    }

                }
            }
        });

    }

    private void reinitializeStates() {
        this.refDecoderDateTime = null;
        this.predDecoderDateTime = null;
        this.initializeBalanceStates(null, null);

    }

    private void initializeStates(DateTime datetime, String balanceState) {
        this.refDecoderDateTime = datetime;
        this.predDecoderDateTime = datetime;
        this.initializeBalanceStates(datetime, balanceState);

    }

    private void initializeBalanceStates(DateTime datetime, String balanceState) {
        this.refBalanceDateTime = datetime;
        this.refBalanceOn = balanceState;
    }

    @Override
    protected void actionInEachFrame(String string) {
        DecoderStatesFrame DecoderStatesFrame = new DecoderStatesFrame("%", string);
        if (DecoderStatesFrame.isValidFrame()) {
            LocalDate actualDecorderLocalDate = DecoderStatesFrame.getDateTocken(FrameTockenKeys.DATE_KEY);
            LocalTime actualDecorderLocalTime = DecoderStatesFrame.getTimeTocken(FrameTockenKeys.TIME_KEY);

            DateTime actualDecorderDateTime = new DateTime(actualDecorderLocalDate.getYear(), actualDecorderLocalDate.getMonthOfYear(), actualDecorderLocalDate.getDayOfMonth(), actualDecorderLocalTime.getHourOfDay(), actualDecorderLocalTime.getMinuteOfHour(), actualDecorderLocalTime.getSecondOfMinute());
            String actualBalanceState = DecoderStatesFrame.getStringTocken(FrameTockenKeys.BALANCE_STATE_KEY);

            if (this.refDecoderDateTime == null) {
                this.initializeStates(actualDecorderDateTime, actualBalanceState);
            } else {
                if (Seconds.secondsBetween(this.predDecoderDateTime, actualDecorderDateTime).getSeconds() > periodSeconds.getSeconds()) {
                    Decoderstate firstdecoderstate = new Decoderstate();
                    firstdecoderstate.setFirstDate(refDecoderDateTime.toDate());
                    firstdecoderstate.setSecondDate(predDecoderDateTime.toDate());
                    firstdecoderstate.setIsdecoderOn(1);

                    Balancestate balancestate = new Balancestate();
                    balancestate.setFirstDate(refBalanceDateTime.toDate());
                    balancestate.setSecondDate(predDecoderDateTime.toDate());
                    if (this.refBalanceOn.equals("OK")) {
                        balancestate.setBalanceState(1);
                    } else {
                        balancestate.setBalanceState(0);
                    }
                    this.balanceStateList.add(balancestate);

                    Decoderstate secondDecoderstate = new Decoderstate();
                    secondDecoderstate.setFirstDate(this.predDecoderDateTime.toDate());
                    secondDecoderstate.setSecondDate(actualDecorderDateTime.toDate());
                    secondDecoderstate.setIsdecoderOn(0);

                    try {
                        this.decoderStateBlockingQueue.put(new Pair<Decoderstate, List< Balancestate>>(firstdecoderstate, new ArrayList<Balancestate>(balanceStateList)));
                        this.decoderStateBlockingQueue.put(new Pair<Decoderstate, List<Balancestate>>(secondDecoderstate, new ArrayList<Balancestate>()));
                    } catch (InterruptedException ex) {
                        Logger.getLogger(MultipleDecoderStatesFramesFile.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    this.balanceStateList.removeAll(balanceStateList);
                    this.refDecoderDateTime = actualDecorderDateTime;
                    this.predDecoderDateTime = actualDecorderDateTime;
                    this.initializeBalanceStates(actualDecorderDateTime, actualBalanceState);

                } else {
                    if (!this.refBalanceOn.equals(actualBalanceState)) {
                        Balancestate balancestate = new Balancestate();
                        balancestate.setFirstDate(refBalanceDateTime.toDate());
                        balancestate.setSecondDate(actualDecorderDateTime.toDate());
                        if (this.refBalanceOn.equals("OK")) {
                            balancestate.setBalanceState(1);
                        } else {
                            balancestate.setBalanceState(0);
                        }
                        this.balanceStateList.add(balancestate);

                        this.initializeBalanceStates(actualDecorderDateTime, actualBalanceState);
                    }

                    this.predDecoderDateTime = actualDecorderDateTime;
                    
                }

            }
        }
    }

    @Override
    protected void lastExecution() {
        while (decoderStateBlockingQueue.size() > 0) {
        }
        databaseExecutorService.shutdown();
    }

}
