package edu.usfca.cs.dfs;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Controller {

    public static void main(String[] args) throws IOException, InterruptedException {
        try {
            System.out.println("Controller listening on port 9999...");
            ServerSocket srvSocket = new ServerSocket(9999);
            Socket socket = srvSocket.accept();
            ObjectOutputStream oos = null;
            StorageMessages.StorageMessageWrapper msgWrapper
                    = StorageMessages.StorageMessageWrapper.parseDelimitedFrom(
                    socket.getInputStream());
            if (msgWrapper.hasStoreChunkMsg()) {
                StorageMessages.StoreChunk storeChunkMsg
                        = msgWrapper.getStoreChunkMsg();
                System.out.println("Request received from client:" + storeChunkMsg.getFileName());
                //create ObjectOutputStream object
                oos = new ObjectOutputStream(socket.getOutputStream());
                //write object to Socket
                oos.writeObject("Hi Client please store " + storeChunkMsg.getFileName() + " at below location");
                //close resources
                oos.close();
            }
            socket.close();
            srvSocket.close();

        } catch (Exception e) {
        }
    }
}


// Very imp
// print file Data at server side
//    ByteString data = storeChunkMsg.getData();
//    byte[] bytedata = data.toByteArray();
//    String s = new String(bytedata);
//                System.out.println("first chunk : "+s);
