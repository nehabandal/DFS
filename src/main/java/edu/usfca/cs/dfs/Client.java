package edu.usfca.cs.dfs;

import com.google.protobuf.ByteString;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


class ReqToController implements Runnable {

    @Override
    public void run() {
        try {
            int count = chunkFile(new File("/Users/npbandal/BigData/p1-nehabandal/studentOutput"));
            System.out.println("Client sending request to Controller on port 9995");
            Socket controllerSoc = new Socket("localhost", 9995);
            StorageMessages.StoreChunk storeChunkMsg = StorageMessages.StoreChunk.newBuilder()
                    .setFileName("Hello Controller! Tell me where I can store " + count + "blocks each of 1 mb")
                    .build();
            StorageMessages.StorageMessageWrapper msgWrapper = StorageMessages.StorageMessageWrapper.newBuilder()
                    .setStoreChunkMsg(storeChunkMsg)
                    .build();
            msgWrapper.writeDelimitedTo(controllerSoc.getOutputStream());
            controllerSoc.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static int chunkFile(File file) throws IOException {
        String fileName = file.getName();
        int chunkcount = 1;
        int sizeOfFiles = 1024 * 1024;
        byte[] buffer = new byte[sizeOfFiles];
        FileInputStream fis = new FileInputStream(file);
        BufferedInputStream bis = new BufferedInputStream(fis);
        while (bis.read(buffer) > 0) {
            chunkcount++;
        }
        return chunkcount;
    }
}

class ResFromController implements Runnable {

    @Override
    public void run() {
        try {
            ServerSocket controllerSocket = new ServerSocket(9996);
            Socket socket = controllerSocket.accept();
            StorageMessages.StorageMessageWrapper msgWrapper
                    = StorageMessages.StorageMessageWrapper.parseDelimitedFrom(
                    socket.getInputStream());
            if (msgWrapper.hasStoreChunkMsg()) {
                StorageMessages.StoreChunk storeChunkMsg
                        = msgWrapper.getStoreChunkMsg();
                System.out.println("Response received from controller: " + storeChunkMsg.getFileName());
            }
            controllerSocket.close();

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}

class StoreFileStorageNode implements Runnable {
    private String msg;

    @Override
    public void run() {
        try {
            String name = Thread.currentThread().getName();
            String file = "/Users/npbandal/BigData/p1-nehabandal/studentOutput";
            int chunkcount = 1;
            int sizeOfFiles = 1024 * 1024;
            byte[] buffer = new byte[sizeOfFiles];
            FileInputStream fis = new FileInputStream(file);
            BufferedInputStream bis = new BufferedInputStream(fis);
            ByteString data = ByteString.copyFrom(buffer);
            System.out.println("Client sending request to Storage Node on port 9997");
            while (bis.read(buffer) > 0) {
                chunkcount++;
                    Socket storageSoc = new Socket("localhost", 9997);
                StorageMessages.StoreChunk storeChunkMsg = StorageMessages.StoreChunk.newBuilder()
                        .setFileName("StudentOutputblk" + chunkcount)
                        .setChunkId(3)
                        .setData(data)
                        .build();
                StorageMessages.StorageMessageWrapper msgWrapper = StorageMessages.StorageMessageWrapper.newBuilder()
                        .setStoreChunkMsg(storeChunkMsg)
                        .build();
                msgWrapper.writeDelimitedTo(storageSoc.getOutputStream());
                storageSoc.close();
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

}

public class Client {
    public static void main(String[] args) throws Exception {
        ReqToController req = new ReqToController();
        ResFromController res = new ResFromController();
        StoreFileStorageNode write = new StoreFileStorageNode();
        Thread t1 = new Thread(req);
        Thread t2 = new Thread(res);
        Thread t3 = new Thread(write);
        t1.start();
        t2.start();
        t3.start();
    }
}


//    private static int chunkFile(File file) throws IOException {
//        String fileName = file.getName();
//        int chunkcount = 1;
//        int sizeOfFiles = 1024 * 1024;
//        byte[] buffer = new byte[sizeOfFiles];
//        int bytesAmount = 0;
//        FileInputStream fis = new FileInputStream(file);
//        BufferedInputStream bis = new BufferedInputStream(fis);
//        while (bis.read(buffer) > 0) {
////            protobuf(buffer, fileName, chunkcount);
//            chunkcount++;
//        }
//        return chunkcount;
//    }
////

