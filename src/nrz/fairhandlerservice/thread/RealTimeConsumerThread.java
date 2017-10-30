/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nrz.fairhandlerservice.thread;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import nrz.bframe.frame.BFrame;
import nrz.fairHandlerStates.bilanciai.ev2000Frame.Ev2000Frame;
import nrz.fairHandlerStates.model.FhFhsStatesModel;
import nrz.fairHandlerStates.model.FhStatesModel;
import nrz.fairHandlerStates.model.GeneralSittingsModel;

/**
 *
 * @author rahimAdmin
 */
public class RealTimeConsumerThread implements Runnable, Observer {

    private final LinkedBlockingQueue<BFrame> realTimeBlockingQueue;
    private ServerSocket serverSocket;
    private Socket socket;
    private ObjectOutputStream objectOutputStream = null;
    private ExecutorService executorService = null;

    public RealTimeConsumerThread(LinkedBlockingQueue<BFrame> realTimeBlockingQueue) {
        this.realTimeBlockingQueue = realTimeBlockingQueue;
        FhStatesModel.getInstance().addObserver(this);
        try {
            this.serverSocket = new ServerSocket(GeneralSittingsModel.getInstance().getLocalNetworkPort());
        } catch (IOException ex) {
            Logger.getLogger(RealTimeConsumerThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void run() {
        Ev2000Frame ev2000Frame = null;

        while (true) {
            try {
                ev2000Frame = (Ev2000Frame) this.realTimeBlockingQueue.take();

            } catch (InterruptedException ex) {
                Logger.getLogger(RealTimeConsumerThread.class.getName()).log(Level.WARNING, ex.getMessage(), ex);
            }

            if (!FhFhsStatesModel.getInstance().isFH_FHS_connectionState()) {
                
                try {
                    socket = serverSocket.accept();
                    objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                    FhFhsStatesModel.getInstance().setFH_FHS_connectionState(true);
                } catch (IOException ex) {
                    Logger.getLogger(RealTimeConsumerThread.class.getName()).log(Level.WARNING, ex.getMessage(), ex);

                    try {
                        if (socket != null) {
                            socket.close();
                        }
                        if (objectOutputStream != null) {
                            objectOutputStream.close();
                        }
                    } catch (IOException ex1) {
                        Logger.getLogger(RealTimeConsumerThread.class.getName()).log(Level.SEVERE, null, ex1);
                    }
                    FhFhsStatesModel.getInstance().setFH_FHS_connectionState(false);
                }

            } else {
                try {

                    this.objectOutputStream.writeObject(ev2000Frame);
                    this.objectOutputStream.flush();
                } catch (IOException ex) {
                    Logger.getLogger(RealTimeConsumerThread.class.getName()).log(Level.SEVERE, null, ex);
                    try {
                        if (socket != null) {
                            this.socket.close();
                        }
                        if (objectOutputStream != null) {
                            this.objectOutputStream.close();
                        }

                    } catch (IOException ex1) {
                        Logger.getLogger(RealTimeConsumerThread.class.getName()).log(Level.SEVERE, null, ex1);
                    }
                    FhFhsStatesModel.getInstance().setFH_FHS_connectionState(false);
                }
            }
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        int notification = (int) arg;

    }
}
