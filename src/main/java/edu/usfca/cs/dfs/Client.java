package edu.usfca.cs.dfs;

import com.google.protobuf.ByteString;

import java.io.*;
import java.net.Socket;

public class Client {
    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {

        byte[] chunk = chunkFile(new File("/Users/npbandal/BigData/p1-nehabandal/studentOutput"));
        String s = new String(chunk);
//        System.out.println("first chunk : " + s);

        Socket sockController = new Socket("localhost", 9999);
        ObjectInputStream ois;
        ByteString data = ByteString.copyFrom(chunk);
        StorageMessages.StoreChunk storeChunkMsg
                = StorageMessages.StoreChunk.newBuilder()
                .setFileName("test.txt")
//                .setChunkId(3)
//                .setData(data)
                .build();
        StorageMessages.StorageMessageWrapper msgWrapper =
                StorageMessages.StorageMessageWrapper.newBuilder()
                        .setStoreChunkMsg(storeChunkMsg)
                        .build();

        msgWrapper.writeDelimitedTo(sockController.getOutputStream());

        //read the Controller response message
        ois = new ObjectInputStream(sockController.getInputStream());
        String message = (String) ois.readObject();
        System.out.println("Message from controller: " + message);
        //close resources
        ois.close();
        Thread.sleep(100);

        Socket sockStorage = new Socket("localhost", 9995);
        ByteString dataStorage = ByteString.copyFrom(chunk);
        StorageMessages.StoreChunk storeChunkMsgStorage
                = StorageMessages.StoreChunk.newBuilder()
                .setFileName("test.txt")
                .setChunkId(3)
                .setData(dataStorage)
                .build();
        StorageMessages.StorageMessageWrapper msgWrapperStorage =
                StorageMessages.StorageMessageWrapper.newBuilder()
                        .setStoreChunkMsg(storeChunkMsgStorage)
                        .build();

        msgWrapperStorage.writeDelimitedTo(sockStorage.getOutputStream());
    }

    private static byte[] chunkFile(File file) throws IOException {
        String fileName = file.getName();
        int chunkcount = 1;
        int sizeOfFiles = 1000000;
        byte[] buffer = new byte[sizeOfFiles];
        int bytesAmount = 0;
        FileInputStream fis = new FileInputStream(file);
        BufferedInputStream bis = new BufferedInputStream(fis);
        while ((bytesAmount = bis.read(buffer)) > 0) {
            String filePartName = String.format("%s_%02d", fileName, chunkcount++);
            File chunkFile = new File(file.getParent(), filePartName);
            try (FileOutputStream out = new FileOutputStream(chunkFile)) {
//                out.write(buffer, 0, bytesAmount);
            }
            return buffer;
        }
        bis.close();
        return buffer;
    }

}

