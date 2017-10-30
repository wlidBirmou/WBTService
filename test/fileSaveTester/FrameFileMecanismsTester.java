/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fileSaveTester;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import nrz.fairHandlerStates.bilanciai.ev2000Frame.Ev2000BFrameState;
import nrz.fairHandlerStates.bilanciai.ev2000Frame.Ev2000Frame;
import nrz.fairHandlerStates.keys.FrameTockenKeys;
import nrz.fairhandlerservice.bilanciai.ev2000.frame.files.FrameInFileParser;
import nrz.fairhandlerservice.thread.ParserConsumerProducerThread;

/**
 *
 * @author rahimAdmin
 */
public class FrameFileMecanismsTester {

    private ArrayList<Path> pathArray;

    public FrameFileMecanismsTester(ArrayList<Path> savePathArray) {
        this.pathArray = savePathArray;
    }

    public FrameFileMecanismsTester() {
        this(new ArrayList<Path>());
    }

    public void makeAllTest() {

    }

    public void testFrameParser() {
        LinkedBlockingQueue<Ev2000Frame> frameBloquingQueue = new LinkedBlockingQueue(1000);
        for (Path path :pathArray) {
            FrameInFileParser.parseMultipleFrameFile(path, frameBloquingQueue);
            for (Ev2000Frame element : frameBloquingQueue) {
                System.out.println(element.getStringTocken(FrameTockenKeys.RCD_CODE_KEY));
            }
            System.out.println(frameBloquingQueue.size());
        }
    }

    public void setSavePathArray(ArrayList<Path> savePathArray) {
        this.pathArray = savePathArray;
    }

    public void addToPathArray(Path path) {
        this.pathArray.add(path);
    }

}
