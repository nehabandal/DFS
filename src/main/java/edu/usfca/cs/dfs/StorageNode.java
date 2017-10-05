package edu.usfca.cs.dfs;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class StorageNode extends ProtoBuf implements Runnable {
    protected Thread runningThread = null;
    protected ServerSocket serverSocket = null;
    protected int serverPort;
    protected boolean isStopped = false;

    public static void main(String[] args)
            throws Exception {
        String hostname = getHostname();

        ProtoBuf pb = new ProtoBuf();
        System.out.println("Starting storage node on " + hostname + "...");
//        pb.protoBufToReceiveRequestFromClientAtStorageNode(9990, "File received ");

        StorageNode server = new StorageNode(9990);
        new Thread(server).start();

        while (true) {
            pb.protoBufToSendHeartbeatFromStorageNodeToController(9010, "Bass01");
            pb.protoBufToSendHeartbeatFromStorageNodeToController(9010, "Bass02");
            pb.protoBufToSendHeartbeatFromStorageNodeToController(9010, "Bass03");
        }

    }

    public StorageNode(int port) {
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
                        "Error accepting file from client ", e);
            }
            try {
                long start = System.currentTimeMillis();
                processClientRequest(clientSocket);
                Thread.sleep(1000);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    private void openServerSocket() {
        try {
            this.serverSocket = new ServerSocket(this.serverPort);
        } catch (IOException e) {
            throw new RuntimeException("Cannot open port 9990", e);
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
            byte[] bytes = storeChunkMsg.toByteArray();
            String s = new String(bytes);
            System.out.println("File is" + s);

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


    /**
     * Retrieves the short host name of the current host.
     *
     * @return name of the current host
     */
    private static String getHostname()
            throws UnknownHostException {
        return InetAddress.getLocalHost().getHostName();
    }


}
