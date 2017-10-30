/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nrz.fairhandlerservice.bilanciai.ev2000.frame.files;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import nrz.fairHandlerStates.files.path.SoftDirectories;
import nrz.fairHandlerStates.keys.FrameTockenKeys;
import nrz.fairHandlerStates.model.FhsStatesModel;
import nrz.fairhandlerservice.thread.ParserConsumerProducerThread;
import org.joda.time.DateTime;
import org.joda.time.Seconds;

/**
 *
 * @author rahimAdmin
 */
public class FrameInFileParser {

    public static void parseMultipleFrameFile(Path filePath, LinkedBlockingQueue databaseBlockingQueue) {
        MultipleEv2000FrameFile multipleFileFrame = new MultipleEv2000FrameFile(databaseBlockingQueue, filePath, FrameTockenKeys.FRAME_DELEMITER_KEY);
        try {
            multipleFileFrame.doAction();
         
        } catch (IOException ex) {
            Logger.getLogger(ParserConsumerProducerThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void parseDecoderStateFile(Path filePath){
        MultipleDecoderStatesFramesFile decoderStatesFramesFile = new MultipleDecoderStatesFramesFile(filePath, FrameTockenKeys.FRAME_DELEMITER_KEY, Seconds.seconds(540));
                try {
                    FhsStatesModel.getInstance().setdBDecoderStateStoreSignalState(true);
                    decoderStatesFramesFile.doAction();
                    filePath.toFile().renameTo(SoftDirectories.EXECUTION_DIRECTORY_PATH.getValue().resolve("saveDecoderState" + DateTime.now().toString("DD-MM-YYYY(HH.mm)") + ".bin").toFile());
//                    Files.delete(filePath);
                    FhsStatesModel.getInstance().setdBDecoderStateStoreSignalState(false);
                } catch (IOException ex) {
                    Logger.getLogger(ParserConsumerProducerThread.class.getName()).log(Level.SEVERE, null, ex);
                }
    }
}
