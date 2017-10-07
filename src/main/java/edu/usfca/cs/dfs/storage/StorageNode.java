package edu.usfca.cs.dfs.storage;

import edu.usfca.cs.dfs.client.ClientProtoBuf;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class StorageNode implements Runnable {
    protected Thread runningThread = null;
    protected ServerSocket serverSocket = null;
    protected int serverPort;
    protected boolean isStopped = false;
    private ClientProtoBuf pb = new ClientProtoBuf();

    public static void main(String[] args)
            throws Exception {
        String hostname = getHostname();

        StorageNode server = new StorageNode(9990);
        server.start();

        System.out.println("Starting storage node on " + hostname + "...");
        server.heartbeat();
    }

    private void start() {
        new Thread(this).start();
    }

    public StorageNode(int port) {
        this.serverPort = port;
    }

    public void heartbeat() {
        while (true) {
            // storageProtobuf.heartbeat();
//            pb.protoBufToSendHeartbeatFromStorageNodeToController(serverPort, "Bass03");
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
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
        StorageProtobuf.StorageMessagePB recfilechunks
                = StorageProtobuf.StorageMessagePB.parseDelimitedFrom(
                clientSocket.getInputStream());
        if (recfilechunks.hasStoreChunkMsg()) {
            StorageProtobuf.StoreChunk storeChunkMsg
                    = recfilechunks.getStoreChunkMsg();
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
