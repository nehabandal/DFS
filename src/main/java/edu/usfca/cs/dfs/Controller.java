package edu.usfca.cs.dfs;

import com.google.protobuf.ByteString;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

class ReqFromClient implements Runnable {
    private ServerSocket srvSocket;

    @Override
    public void run() {
        try {

            System.out.println("Controller listening on port 9995...");
            srvSocket = new ServerSocket(9995);
            Socket socket = srvSocket.accept();
            StorageMessages.StorageMessageWrapper msgWrapper
                    = StorageMessages.StorageMessageWrapper.parseDelimitedFrom(
                    socket.getInputStream());
            if (msgWrapper.hasStoreChunkMsg()) {
                StorageMessages.StoreChunk storeChunkMsg
                        = msgWrapper.getStoreChunkMsg();
                System.out.println("Request received from client:" + storeChunkMsg.getFileName());
                srvSocket.close();
            }
        } catch (Exception e) {
        }
    }
}

class ResToClient implements Runnable {
    private Socket clientsoc;
    @Override
    public void run() {
        try {
            clientsoc = new Socket("localhost", 9996);
            System.out.println("Response to client from controller 9996");
            ByteString data = ByteString.copyFromUtf8("Use recent host to store data");
            StorageMessages.StoreChunk storeChunkMsg = StorageMessages.StoreChunk.newBuilder()
                    .setFileName("Use recent host to store data")
                    .build();
            StorageMessages.StorageMessageWrapper msgWrapper = StorageMessages.StorageMessageWrapper.newBuilder()
                    .setStoreChunkMsg(storeChunkMsg)
                    .build();
            msgWrapper.writeDelimitedTo(clientsoc.getOutputStream());
            clientsoc.close();
        } catch (Exception e) {
        }
    }
}


public class Controller {
    public static void main(String[] args) throws IOException, InterruptedException {
        ReqFromClient req = new ReqFromClient();
        ResToClient res = new ResToClient();
        Thread t1 = new Thread(req);
        Thread t2 = new Thread(res);
        t1.start();
        t2.start();
    }
}
