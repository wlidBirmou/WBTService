/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nrz.fairhandlerservice.files.filesWatcher;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.util.logging.Level;
import java.util.logging.Logger;
import nrz.fairHandlerStates.model.GeneralSittingsModel;
import nrz.fairHandlerStates.files.path.SoftDirectories;
import nrz.fairHandlerStates.files.path.SoftFiles;
import nrz.nFiles.watchfile.WatchFiles;
import org.jdom2.JDOMException;

/**
 *
 * @author rahimAdmin
 */
public class SittingFilesDirectoryWatcher extends WatchFiles {

    public SittingFilesDirectoryWatcher() throws IOException {
        super();
        SoftDirectories.GENERAL_SITTING_DIRECTORY_PATH.getValue().register(watcher, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY);

    }

    @Override
    public void defineAction(WatchKey key, Path filePath, WatchEvent.Kind<?> kindEvent) {
        Path directoryPath = (Path) key.watchable();
        if (directoryPath.startsWith(SoftDirectories.GENERAL_SITTING_DIRECTORY_PATH.getValue()) && SoftDirectories.GENERAL_SITTING_DIRECTORY_PATH.getValue().startsWith(directoryPath)) {
            if (filePath.equals(SoftFiles.GENERAL_SITTINGS_FILE_PATH.getValue().getFileName())) {
                if (kindEvent == StandardWatchEventKinds.ENTRY_CREATE) {
                    try {
                        GeneralSittingsModel.getInstance().loadGeneralSittingDocument();
                    } catch (JDOMException | IOException ex) {
                        Logger.getLogger(SittingFilesDirectoryWatcher.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else if (kindEvent == StandardWatchEventKinds.ENTRY_MODIFY) {
                    try {
                        GeneralSittingsModel.getInstance().loadGeneralSittingDocument();
                    } catch (JDOMException | IOException ex) {
                        Logger.getLogger(SittingFilesDirectoryWatcher.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }

    }
}
