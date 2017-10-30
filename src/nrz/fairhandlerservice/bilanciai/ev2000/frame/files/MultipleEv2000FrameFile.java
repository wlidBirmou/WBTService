/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nrz.fairhandlerservice.bilanciai.ev2000.frame.files;

import java.nio.file.Path;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import nrz.bframe.multipleFrame.MultipleFileBFrame;
import nrz.fairHandlerStates.bilanciai.ev2000Frame.Ev2000Frame;

/**
 *
 * @author rahimAdmin
 */
public class MultipleEv2000FrameFile extends MultipleFileBFrame {

    private final LinkedBlockingQueue<Ev2000Frame> databaseBlockingQueue;

    public MultipleEv2000FrameFile(LinkedBlockingQueue<Ev2000Frame> databaseBlockingQueue, Path filePath, char delimiter) {
        super(filePath, delimiter);
        this.databaseBlockingQueue = databaseBlockingQueue;
    }

    @Override
    protected void actionInEachFrame(String string) {
        Ev2000Frame ev2000Frame=new Ev2000Frame("%", string);
        if(ev2000Frame.isValidFrame()){
        try {
            this.databaseBlockingQueue.put(ev2000Frame);
        } catch (InterruptedException ex) {
            Logger.getLogger(MultipleEv2000FrameFile.class.getName()).log(Level.SEVERE, null, ex);
        }}
    }

}
