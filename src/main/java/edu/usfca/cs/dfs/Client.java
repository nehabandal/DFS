package edu.usfca.cs.dfs;

import com.google.protobuf.ByteString;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;

public class Client {
    public static void main(String[] args) throws Exception {
        chunkFile(new File("/Users/npbandal/BigData/p1-nehabandal/studentOutput8000"));

    }

    private static void chunkFile(File file) throws IOException {
        String fileName = file.getName();
        int chunkcount = 1;
        int sizeOfFiles = 1024 * 1024;
        byte[] buffer = new byte[sizeOfFiles];
        int bytesAmount = 0;
        FileInputStream fis = new FileInputStream(file);
        BufferedInputStream bis = new BufferedInputStream(fis);
        while ((bytesAmount = bis.read(buffer)) > 0) {
            String filePartName = String.format("%s_%02d", fileName, chunkcount++);
            File chunkFile = new File(file.getParent(), filePartName);
            try (FileOutputStream out = new FileOutputStream(chunkFile)) {
                out.write(buffer, 0, bytesAmount);
            }
            protobuf(chunkFile);
        }
    }

    private static void protobuf(File chunkFile) throws IOException {
        Socket sock = new Socket("localhost", 9999);
        byte[] bytesFile = Files.readAllBytes(chunkFile.toPath());
        ByteString data = ByteString.copyFrom(bytesFile);

        StorageMessages.StoreChunk storeChunkMsg = StorageMessages.StoreChunk.newBuilder()
                .setFileName("chunkFile.txt")
                .setChunkId(3)
                .setData(data)
                .build();

        StorageMessages.StorageMessageWrapper msgWrapper =
                StorageMessages.StorageMessageWrapper.newBuilder()
                        .setStoreChunkMsg(storeChunkMsg)
                        .build();
        msgWrapper.writeDelimitedTo(sock.getOutputStream());

        sock.close();
    }
}


