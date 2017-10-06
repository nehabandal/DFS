package edu.usfca.cs.dfs;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Controller extends ProtoBuf implements Runnable {
    protected Thread runningThread = null;
    protected ServerSocket serverSocket = null;
    protected int serverPort = 9010;
    protected boolean isStopped = false;

    public static void main(String[] args) throws IOException, InterruptedException {
        Controller server = new Controller(9010);
        new Thread(server).start();
        List<String> hostNames = Arrays.asList("Bass1", "Bass2", "Bass3");
        ProtoBuf pb = new ProtoBuf();
        HashMap<String,String> hostdetails= new HashMap<String,String>();
        hostdetails.put("ML-ITS-601927","10.1.25.209");
        hostdetails.put("Ganesha","1254235");
        System.out.println("Controller listening on port 9998...");
        pb.protoBufToReceiveRequestFromClientAtController(9998, "Request received from client ");
        pb.protoBufToSendResponseToClientFromController(9999,hostdetails);

//        try {
//            Thread.sleep(10 * 1000);
//            pb.protoBufToSendResponseToClientFromController(9999, hostNames);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        System.out.println("Stopping Server");
//        server.stop();

    }

    public Controller(int port) {
        this.serverPort = port;
    }

    @Override
    public void run() {
        synchronized (this) {
            this.runningThread = Thread.currentThread();
        }
        openServerSocket();

        while (!isStopped()) {
            Socket clientSocket = null;
            try {
                clientSocket = this.serverSocket.accept();
            } catch (IOException e) {
                if (isStopped()) {
                    return;
                }
                throw new RuntimeException(
                        "Error accepting client connection", e);
            }
            try {
                long start = System.currentTimeMillis();
                processClientRequest(clientSocket);
                Thread.sleep(3000);
                if (isStopped()) {
                    stop();
                }
                int timeToSleep = (int) (System.currentTimeMillis() - start);
                if (timeToSleep > 5000) {
                    return;
                }

            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    private void openServerSocket() {
        try {
            this.serverSocket = new ServerSocket(this.serverPort);
        } catch (IOException e) {
            throw new RuntimeException("Cannot open port 9010", e);
        }
    }

    private void processClientRequest(Socket clientSocket)
            throws IOException {
        StorageMessages.StorageMessageWrapper msgWrapper
                = StorageMessages.StorageMessageWrapper.parseDelimitedFrom(
                clientSocket.getInputStream());
        if (msgWrapper.hasStoreChunkMsg()) {
            StorageMessages.StoreChunk storeChunkMsg
                    = msgWrapper.getStoreChunkMsg();
            System.out.println("Host Alive " + storeChunkMsg.getFileName());
        }
    }

    private synchronized boolean isStopped() {
        return this.isStopped;
    }

    public synchronized void stop() {
        this.isStopped = true;
        try {
            this.serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException("Error closing server", e);
        }
    }


}
