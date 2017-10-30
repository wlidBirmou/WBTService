/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nrz.fairhandlerservice.thread;

import nrz.bframe.frameTocken.defaultFrameTocken.BitSetFrameTocken;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import nrz.fairHandlerStates.bilanciai.ev2000Frame.Ev2000Frame;
import nrz.fairHandlerStates.files.path.SoftDirectories;
import nrz.fairHandlerStates.keys.CommandsKeys;
import nrz.fairHandlerStates.keys.FrameTockenKeys;
import nrz.fairHandlerStates.model.FhsStatesModel;
import nrz.fairhandlerservice.bilanciai.ev2000.frame.files.FrameInFileParser;
import org.javatuples.Pair;
import org.joda.time.DateTime;

/**
 *
 * @author rahimAdmin
 */
public class ParserConsumerProducerThread implements Runnable {

    private final LinkedBlockingQueue<Pair<String, Object>> balanceProducerBlockingQueue;
    private final LinkedBlockingQueue<Ev2000Frame> databaseBlockingQueue;
    private final LinkedBlockingQueue<Ev2000Frame> realTimeBlockingQueue;

    private boolean databaseFlag = false;

    public ParserConsumerProducerThread(LinkedBlockingQueue<Pair<String, Object>> balanceProducerBlockingQueue, LinkedBlockingQueue databaseBlockingQueue, LinkedBlockingQueue realTimeBlockingQueue) {
        this.balanceProducerBlockingQueue = balanceProducerBlockingQueue;
        this.databaseBlockingQueue = databaseBlockingQueue;
        this.realTimeBlockingQueue = realTimeBlockingQueue;
    }

    @Override
    public void run() {
        Ev2000Frame balanceFrame;

        while (true) {
            try {

                Pair<String, Object> pair = this.balanceProducerBlockingQueue.take();
                switch (pair.getValue0()) {
                    case (CommandsKeys.CYCLIC_FRAME_KEY):

                        balanceFrame = (Ev2000Frame) pair.getValue1();
                        this.realTimeBlockingQueue.put(balanceFrame);
                        this.parseRealTimeFrameToDatabase(balanceFrame);
                        break;
                    case (CommandsKeys.SEND_SAVE_FRAME_FILE_KEY):
                        Path filePath = (Path) pair.getValue1();
                        this.parseMultipleFrameFileInExecutor(filePath);
                        break;
                    case (CommandsKeys.SEND_SAVE_DECODER_STATE_FILE_KEY):
                        Path filePath1 = (Path) pair.getValue1();
                        this.parseMultipleDecoderStateFrameAndDeleteFile(filePath1);

                        break;
                    case (CommandsKeys.CYCLIC_FRAME_TEST_KEY):
                        balanceFrame = (Ev2000Frame) pair.getValue1();
                        this.realTimeBlockingQueue.put(balanceFrame);
                        this.parseRealTimeFrameToDatabase(balanceFrame);
                        break;
                }

            } catch (InterruptedException ex) {
                Logger.getLogger(ParserConsumerProducerThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void parseRealTimeFrameToDatabase(Ev2000Frame balanceFrame) {

        BitSetFrameTocken reducedStateFrameTocken = (BitSetFrameTocken) balanceFrame.getTocken(FrameTockenKeys.REDUCED_FRAME_STATE_KEY);
        if (reducedStateFrameTocken.getBitset(FrameTockenKeys.PRINT_REQUEST_KEY)) {
            if (!this.databaseFlag) {
                try {
                    this.databaseBlockingQueue.put(balanceFrame);
                    this.databaseFlag = true;
                } catch (InterruptedException ex) {
                    Logger.getLogger(ParserConsumerProducerThread.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } else {
            this.databaseFlag = false;
        }
    }

    private void parseMultipleFrameFileInExecutor(Path filePath) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(new Runnable() {

            @Override
            public void run() {
                FrameInFileParser.parseMultipleFrameFile(filePath, databaseBlockingQueue);
                executorService.shutdown();
            }
        });
    }

    private void parseMultipleDecoderStateFrameAndDeleteFile(Path filePath) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(new Runnable() {

            @Override
            public void run() {
                FhsStatesModel.getInstance().setdBWeightStoreSignalState(true);
                FrameInFileParser.parseDecoderStateFile(filePath);
                filePath.toFile().renameTo(SoftDirectories.EXECUTION_DIRECTORY_PATH.getValue().resolve("saveFrame" + DateTime.now().toString("DD-MM-YYYY(HH.mm)") + ".bin").toFile());
//                    Files.delete(filePath);
                FhsStatesModel.getInstance().setdBWeightStoreSignalState(false);
                executorService.shutdown();
            }
        });
    }
}
