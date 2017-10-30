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
import nrz.fairHandlerStates.files.path.SoftDirectories;
import nrz.fairHandlerStates.files.path.SoftFiles;
import nrz.fairHandlerStates.model.FhStatesModel;
import nrz.nFiles.watchfile.WatchFiles;

/**
 *
 * @author rahimAdmin
 */
public class ExecutionDirectoryWatcher extends WatchFiles {

    public ExecutionDirectoryWatcher() throws IOException {
        super();
        SoftDirectories.EXECUTION_DIRECTORY_PATH.getValue().register(watcher, StandardWatchEventKinds.ENTRY_CREATE,StandardWatchEventKinds.ENTRY_MODIFY);
    }

    @Override
    public void defineAction(WatchKey key, Path filePath, WatchEvent.Kind<?> kindEvent) {
           Path directoryPath = (Path) key.watchable();
        if (directoryPath.startsWith(SoftDirectories.EXECUTION_DIRECTORY_PATH.getValue()) && SoftDirectories.EXECUTION_DIRECTORY_PATH.getValue().startsWith(directoryPath)) {
            if (filePath.equals(SoftFiles.FH_STATES_FILE_PATH.getValue().getFileName())) {
                if (kindEvent == StandardWatchEventKinds.ENTRY_CREATE) {
                    try {
                        FhStatesModel.getInstance().loadStatesFile();
                    } catch (IOException|ClassNotFoundException ex) {
                        Logger.getLogger(ExecutionDirectoryWatcher.class.getName()).log(Level.SEVERE, null, ex);
                        
                    } 
                } else if (kindEvent == StandardWatchEventKinds.ENTRY_MODIFY) {
                    try {
                        FhStatesModel.getInstance().loadStatesFile();
                    } catch (IOException|ClassNotFoundException ex) {
                        Logger.getLogger(ExecutionDirectoryWatcher.class.getName()).log(Level.SEVERE, null, ex);
                    } 
                }
            }       
        }
    }

}
