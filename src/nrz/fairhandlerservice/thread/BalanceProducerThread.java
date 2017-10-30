/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nrz.fairhandlerservice.thread;

import nrz.bframe.frame.BFrameCommands;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import nrz.fairHandlerStates.bilanciai.ev2000Frame.Ev2000Frame;
import nrz.fairHandlerStates.model.FhsStatesModel;
import nrz.fairHandlerStates.keys.CommandsKeys;
import nrz.fairHandlerStates.model.GeneralSittingsModel;
import nrz.fairHandlerStates.files.path.SoftFiles;
import nrz.fairHandlerStates.keys.MessageKeys;
import nrz.fairhandlerservice.thread.test.TestMethods;
import nrz.patternImplementation.ObjectPoolPattern.socketpool.WrappedSocket;
import org.javatuples.Pair;

/**
 *
 * @author rahimAdmin
 */
public class BalanceProducerThread implements Runnable, Observer {

    private final LinkedBlockingQueue<Pair<String, Object>> balanceProducerBlockingQueue;
    private ScheduledExecutorService scheduledExecutorService;
    private ThreadsMonitor threadsMonitor;
    private int timeOut = 10000;
    WrappedSocket realTimeSocket;

    public BalanceProducerThread(LinkedBlockingQueue<Pair<String, Object>> balanceProducerBlockingQueue) {
        this.balanceProducerBlockingQueue = balanceProducerBlockingQueue;
        this.scheduledExecutorService = new ScheduledThreadPoolExecutor(3);
        this.threadsMonitor = new ThreadsMonitor();
        GeneralSittingsModel.getInstance().addObserver(this);
        FhsStatesModel.getInstance().addObserver(this);
    }

    @Override
    public void run() {

        byte[] readerBuffer = new byte[8];
        Ev2000Frame balanceFrame;
        String readedMessage;

//        this.firstBalanceConnection();

        while (true) {
            while (threadsMonitor.isWaiting()) {
                try {
                    this.realTimeSocket.closeAllConnection();
                } catch (IOException ex) {
                    Logger.getLogger(BalanceProducerThread.class.getName()).log(Level.SEVERE, null, ex);
                }
                FhsStatesModel.getInstance().setBalanceConnectedState(false);
                this.threadsMonitor.doNotify();
                this.threadsMonitor.doWait();
            }
            if (!FhsStatesModel.getInstance().isBalanceConnectedState()) {
                try {
                    this.realTimeSocket = new WrappedSocket();
                    this.realTimeSocket.setSoTimeout(timeOut);
                    this.realTimeSocket.connect(new InetSocketAddress("127.0.0.1", GeneralSittingsModel.getInstance().getBalanceConverterPort()));
                    FhsStatesModel.getInstance().setBalanceConnectedState(true);
                } catch (IOException ex) {
                    try {
                        this.realTimeSocket.closeAllConnection();
                    } catch (IOException ex1) {
                        Logger.getLogger(BalanceProducerThread.class.getName()).log(Level.SEVERE, null, ex1);
                    }
                    Logger.getLogger(BalanceProducerThread.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                try {
                    realTimeSocket.getBufferedOutputStream().write(BFrameCommands.getInstance().getCommand(CommandsKeys.CYCLIC_FRAME_KEY));
                    realTimeSocket.getBufferedOutputStream().flush();
                    readedMessage = "";
                    readerBuffer = new byte[128];
                    int i = 0;
                    boolean stillSending = false;
                    FhsStatesModel.getInstance().setReceiveBalanceFrameState(true);
                    i = realTimeSocket.getBufferedInputStream().read(readerBuffer);
                    readedMessage = readedMessage + new String(readerBuffer, 0, i);
                    if (readedMessage.charAt(0) == MessageKeys.BEGIN_MESSAGE_KEY) {
                        readedMessage = readedMessage.substring(1, readedMessage.length());
                        if (readedMessage.isEmpty() || readedMessage.charAt(readedMessage.length() - 1) != MessageKeys.END_MESSAGE_KEY) {
                            stillSending = true;
                            while (stillSending) {
                                i = realTimeSocket.getBufferedInputStream().read(readerBuffer);
                                readedMessage = readedMessage + new String(readerBuffer, 0, i);
                                if (readedMessage.charAt(readedMessage.length() - 1) == MessageKeys.END_MESSAGE_KEY) {
                                    stillSending = false;
                                }
                            }
                        }
                        readedMessage = readedMessage.substring(0, readedMessage.length() - 1);
                        FhsStatesModel.getInstance().setReceiveBalanceFrameState(true);
                        System.out.println(readedMessage);
                        TestMethods.storeFrameInFile(readedMessage);
                        balanceFrame = new Ev2000Frame("%", readedMessage);
                        try {
                            if (balanceFrame.isValidFrame()) {
                                balanceProducerBlockingQueue.put(new Pair(CommandsKeys.CYCLIC_FRAME_KEY, balanceFrame));
                            } else {
                                if (!FhsStatesModel.getInstance().isValidBalanceFrameState()) {
                                    TimeUnit.MILLISECONDS.sleep(200);
                                    this.realTimeSocket.getBufferedOutputStream().write(BFrameCommands.getInstance().getCommand(CommandsKeys.ORDER_TERMINAL_TO_CYCLIC_MODE));
                                    this.realTimeSocket.getBufferedOutputStream().flush();
                                }

                            }
                        } catch (InterruptedException ex) {
                            Logger.getLogger(BalanceProducerThread.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    try {
                        TimeUnit.MILLISECONDS.sleep(200);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(BalanceProducerThread.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } catch (IOException ex) {
                    Logger.getLogger(BalanceProducerThread.class.getName()).log(Level.SEVERE, null, ex);
                    FhsStatesModel.getInstance().setBalanceConnectedState(false);
                    try {
                        this.realTimeSocket.closeAllConnection();
                    } catch (IOException ex1) {
                        Logger.getLogger(BalanceProducerThread.class.getName()).log(Level.SEVERE, null, ex1);
                    }
                }

            }
        }
    }

    private void firstBalanceConnection() {
        while (!FhsStatesModel.getInstance().isBalanceConnectedState()) {
            try {
                this.realTimeSocket = new WrappedSocket();
                this.realTimeSocket.setSoTimeout(timeOut);
                this.realTimeSocket.connect(new InetSocketAddress(InetAddress.getByName(GeneralSittingsModel.getInstance().getBalanceConverterIP()), GeneralSittingsModel.getInstance().getBalanceConverterPort()));
                FhsStatesModel.getInstance().setBalanceConnectedState(true);
            } catch (IOException ex) {
                Logger.getLogger(BalanceProducerThread.class.getName()).log(Level.SEVERE, null, ex);

            } finally {
                try {
                    this.realTimeSocket.closeAllConnection();
                } catch (IOException ex1) {
                    Logger.getLogger(BalanceProducerThread.class.getName()).log(Level.SEVERE, null, ex1);
                }
            }
        }

//        this.fileExecutorService(CommandsKeys.IS_AVAILABLE_SAVE_FRAME_FILE_KEY, CommandsKeys.SEND_SAVE_FRAME_FILE_KEY, CommandsKeys.REMOVE_SAVE_FRAME_FILE_KEY, SoftFiles.SAVE_FRAME_FILE_PATH, 0, 2, TimeUnit.HOURS);
//        this.fileExecutorService(CommandsKeys.IS_AVAILABLE_SAVE_DECODER_STATE_FILE_KEY, CommandsKeys.SEND_SAVE_DECODER_STATE_FILE_KEY, CommandsKeys.REMOVE_SAVE_DECODER_STATE_FRAME_FILE_KEY, SoftFiles.SAVE_DECODER_STATES_FILE_PATH, 0, 1, TimeUnit.HOURS);
////
    }

    private void fileExecutorService(String firstCommandKey, String secondCommandKey, String thirdCommandKey, SoftFiles softFile, int beginDelay, int repetitionPeriod, TimeUnit timeUnit) {
        this.scheduledExecutorService.scheduleAtFixedRate(new Runnable() {

            @Override
            public void run() {
                if (FhsStatesModel.getInstance().isBalanceConnectedState()) {
                    threadsMonitor.doWait();
                    WrappedSocket fileSocket = null;

                    try {

                        fileSocket = new WrappedSocket();
                        fileSocket.setSoTimeout(20000);
                        fileSocket.connect(new InetSocketAddress(InetAddress.getByName(GeneralSittingsModel.getInstance().getBalanceConverterIP()), GeneralSittingsModel.getInstance().getBalanceConverterPort()));
                        FhsStatesModel.getInstance().setBalanceConnectedState(true);

                        fileSocket.getBufferedOutputStream().write(BFrameCommands.getInstance().getCommand(firstCommandKey));
                        fileSocket.getBufferedOutputStream().flush();

                        byte[] confirmationArrayByte = new byte[100];
                        System.out.println("reading file frame" + new String(confirmationArrayByte));
                        fileSocket.getBufferedInputStream().read(confirmationArrayByte);
                        System.out.println(new String(confirmationArrayByte));
                        if (confirmationArrayByte.equals(BFrameCommands.getInstance().getCommand(CommandsKeys.NO_KEY))) {
                            return;
                        } else {
                            Files.deleteIfExists(softFile.getValue());
                            Files.createFile(softFile.getValue());
                            RandomAccessFile accessFile = new RandomAccessFile(softFile.getValue().toFile(), "rw");

                            fileSocket.getBufferedOutputStream().write(BFrameCommands.getInstance().getCommand(secondCommandKey));
                            fileSocket.getBufferedOutputStream().flush();
                            byte[] byteBuffer = new byte[1024];
                            int i = 0;
                            boolean stillSending = false;
                            i = fileSocket.getBufferedInputStream().read(byteBuffer);
                            String readedMessage = new String(byteBuffer, 0, i);

                            if (readedMessage.charAt(0) == MessageKeys.BEGIN_MESSAGE_KEY) {
                                readedMessage = readedMessage.substring(1, readedMessage.length());
                                if (readedMessage.isEmpty() || readedMessage.charAt(readedMessage.length() - 1) != MessageKeys.END_MESSAGE_KEY) {
                                    stillSending = true;
                                    while (stillSending) {
                                        i = fileSocket.getBufferedInputStream().read(byteBuffer);
                                        readedMessage = new String(byteBuffer, 0, i);
                                        if (readedMessage.charAt(readedMessage.length() - 1) == MessageKeys.END_MESSAGE_KEY) {
                                            stillSending = false;
                                            readedMessage = readedMessage.substring(0, readedMessage.length() - 1);
                                        }
                                        accessFile.write(readedMessage.getBytes());
                                        System.out.print(readedMessage);

                                    }
                                }
                                accessFile.close();
                                try {
                                    boolean supressionFlag = false;
                                    try {
                                        TimeUnit.MILLISECONDS.sleep(200);
                                    } catch (InterruptedException ex) {
                                        Logger.getLogger(BalanceProducerThread.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                    fileSocket.getBufferedOutputStream().write(BFrameCommands.getInstance().getCommand(thirdCommandKey));
                                    fileSocket.getBufferedOutputStream().flush();

                                    i = fileSocket.getBufferedInputStream().read(byteBuffer);
                                    readedMessage = new String(byteBuffer, 0, i);
                                    if (readedMessage.equals("<RS>") || readedMessage.equals("<NR>")) {
                                        supressionFlag = true;
                                    }
                                    if (supressionFlag) {
                                        balanceProducerBlockingQueue.put(new Pair(secondCommandKey, softFile.getValue()));
                                    }
                                } catch (InterruptedException ex) {
                                    Logger.getLogger(BalanceProducerThread.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            } else {
                                accessFile.close();
                            }
                        }
                    } catch (IOException ex) {
                        Logger.getLogger(BalanceProducerThread.class.getName()).log(Level.SEVERE, null, ex);
                    } finally {
                        try {
                            FhsStatesModel.getInstance().setBalanceConnectedState(false);
                            fileSocket.closeAllConnection();
                        } catch (IOException ex) {
                            Logger.getLogger(BalanceProducerThread.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    threadsMonitor.doNotify();

                }
            }
        }, beginDelay, repetitionPeriod, timeUnit);

    }

    @Override

    public void update(Observable o, Object arg) {
        int notification = (int) arg;
        if (o instanceof GeneralSittingsModel) {
            switch (notification) {
                case (GeneralSittingsModel.BALANCE_CONVERTER_IP_NOTIFIER):
                    FhsStatesModel.getInstance().setBalanceConnectedState(true);
                    try {
                        realTimeSocket.closeAllConnection();
                    } catch (IOException ex) {
                        Logger.getLogger(BalanceProducerThread.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    break;
                case (GeneralSittingsModel.BALANCE_CONVERTER_PORT_NOTIFIER):
                    FhsStatesModel.getInstance().setBalanceConnectedState(true);
                    try {
                        realTimeSocket.closeAllConnection();
                    } catch (IOException ex) {
                        Logger.getLogger(BalanceProducerThread.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    break;
            }

        }
        if (o instanceof FhsStatesModel) {
            switch (notification) {
                case (FhsStatesModel.VALID_BALANCE_FRAME_STATE):
                    ExecutorService service = Executors.newSingleThreadExecutor();
                    service.execute(new Runnable() {
                        @Override
                        public void run() {
                            if (!FhsStatesModel.getInstance().isValidBalanceFrameState()) {
                                WrappedSocket socket = null;
                                try {
                                    threadsMonitor.doWait();
                                    socket = new WrappedSocket();
                                    socket.setSoTimeout(20000);
                                    socket.connect(new InetSocketAddress(InetAddress.getByName(GeneralSittingsModel.getInstance().getBalanceConverterIP()), GeneralSittingsModel.getInstance().getBalanceConverterPort()));
                                    FhsStatesModel.getInstance().setBalanceConnectedState(true);
                                    socket.getBufferedOutputStream().write(BFrameCommands.getInstance().getCommand(CommandsKeys.ORDER_TERMINAL_TO_CYCLIC_MODE));
                                    socket.getBufferedOutputStream().flush();
                                } catch (SocketException ex) {
                                    Logger.getLogger(BalanceProducerThread.class.getName()).log(Level.SEVERE, null, ex);
                                } catch (UnknownHostException ex) {
                                    Logger.getLogger(BalanceProducerThread.class.getName()).log(Level.SEVERE, null, ex);
                                } catch (IOException ex) {
                                    Logger.getLogger(BalanceProducerThread.class.getName()).log(Level.SEVERE, null, ex);
                                } finally {
                                    try {
                                        socket.closeAllConnection();
                                        FhsStatesModel.getInstance().setBalanceConnectedState(false);
                                    } catch (IOException ex) {
                                        Logger.getLogger(BalanceProducerThread.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                    threadsMonitor.doNotify();
                                    service.shutdown();
                                }
                            }
                        }
                    });
                    break;
            }
        }
    }

}

class ThreadsMonitor {

    private Object monitor;
    private boolean notifySignal;
    private int waitingCounter;

    public ThreadsMonitor() {
        this.monitor = new Object();
        this.notifySignal = false;
        this.waitingCounter = 0;
    }

    public void doWait() {
        synchronized (this.monitor) {
            this.waitingCounter++;
            while (!notifySignal) {
                try {
                    this.monitor.wait();
                } catch (InterruptedException ex) {
                    Logger.getLogger(ThreadsMonitor.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            this.notifySignal = false;
            this.waitingCounter--;
        }
    }

    public void doNotify() {
        synchronized (this.monitor) {
            this.notifySignal = true;
            this.monitor.notify();
        }
    }

    public synchronized boolean isWaiting() {
        if (this.waitingCounter > 0) {
            return true;
        } else {
            return false;
        }
    }

}
