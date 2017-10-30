/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nrz.fairhandlerservice.thread.test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;
import nrz.fairHandlerStates.files.path.SoftDirectories;

/**
 *
 * @author rahimAdmin
 */
public class TestMethods {

    private static RandomAccessFile accessFile;
    private final static Path realTimeFrame = SoftDirectories.EXECUTION_DIRECTORY_PATH.getValue().resolve("storedRealTimeFrame.bin");

    public static void storeFrameInFile(String frame) {
        try {
            RandomAccessFile accessFile =TestMethods.getAccessFile();
            accessFile.seek(realTimeFrame.toFile().length());
            accessFile.writeChars(frame+"$");
        } catch (IOException ex) {
            Logger.getLogger(TestMethods.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    private static RandomAccessFile getAccessFile() {
        if (accessFile == null) {
            try {
                accessFile=new RandomAccessFile(realTimeFrame.toFile(), "rw");
            } catch (FileNotFoundException ex) {
                Logger.getLogger(TestMethods.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return accessFile;
    }
}
