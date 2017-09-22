package edu.usfca.cs.dfs;

import com.google.protobuf.ByteString;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;

import static java.lang.System.out;

public class Client {
    public static void main(String[] args) throws Exception {
        chunkFile(new File("/Users/npbandal/BigData/p1-nehabandal/studentOutput"));

    }

    private static void chunkFile(File file) throws IOException {
        String fileName = file.getName();
        int chunkcount = 1;
        int sizeOfFiles = 1024 * 1024;
        byte[] buffer = new byte[sizeOfFiles];
        int bytesAmount = 0;
        FileInputStream fis = new FileInputStream(file);
        BufferedInputStream bis = new BufferedInputStream(fis);
        while (bis.read(buffer) > 0) {
            out.write(buffer, 0, bytesAmount);
            protobuf(buffer, fileName, chunkcount);
            chunkcount++;
        }
    }

    private static void protobuf(byte[] chunkFile, String fileName, int count) throws IOException {
        Socket sock = new Socket("localhost", 9999);
//        byte[] bytesFile = Files.readAllBytes(chunkFile.toPath());
        ByteString data = ByteString.copyFrom(chunkFile);

        StorageMessages.StoreChunk storeChunkMsg = StorageMessages.StoreChunk.newBuilder()
                .setFileName(fileName + "Blk" + count)
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

