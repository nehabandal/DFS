package edu.usfca.cs.dfs;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by npbandal on 10/1/17.
 */
public class ProtoBuf implements ChunkHelper {


    public void protoBufToSendReq(int portnumber, String fileName) {
        try {
            Socket sockController = new Socket("localhost", portnumber);
            System.out.println(fileName);
            StorageMessages.StoreChunk storeChunkMsg
                    = StorageMessages.StoreChunk.newBuilder()
                    .setFileName(fileName)
                    .build();
            StorageMessages.StorageMessageWrapper msgWrapper =
                    StorageMessages.StorageMessageWrapper.newBuilder()
                            .setStoreChunkMsg(storeChunkMsg)
                            .build();
            msgWrapper.writeDelimitedTo(sockController.getOutputStream());
            sockController.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void protoBufToReceiveResponse(int portnumber, String msg) throws IOException {
        ServerSocket srvSocket = new ServerSocket(portnumber);
        try {
            while (!srvSocket.isClosed()) {
                Socket client = srvSocket.accept();
                StorageMessages.StorageMessageWrapper msgWrapper
                        = StorageMessages.StorageMessageWrapper.parseDelimitedFrom(
                        client.getInputStream());
                if (msgWrapper.hasStoreChunkMsg()) {
                    StorageMessages.StoreChunk storeChunkMsg
                            = msgWrapper.getStoreChunkMsg();
                    System.out.println(msg + storeChunkMsg.getFileName());
                }
            }
            srvSocket.close();
        }
        finally {
            srvSocket.close();
        }
    }

    @Override
    public List<File> splitFile(File file) {
        int counter = 1;
        List<File> files = new ArrayList<File>();
        int sizeOfChunk = 1024 * 1024;
        String eof = System.lineSeparator();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String name = file.getName();
            String line = br.readLine();
            while (line != null) {
                File newFile = new File(file.getParent(), name + "."
                        + String.format("%02d", counter++));
                try (OutputStream out = new BufferedOutputStream(new FileOutputStream(newFile))) {
                    int fileSize = 0;
                    while (line != null) {
                        byte[] bytes = (line + eof).getBytes(Charset.defaultCharset());
                        if (fileSize + bytes.length > sizeOfChunk)
                            break;
                        out.write(bytes);
                        fileSize += bytes.length;
                        line = br.readLine();
                    }
                }
                files.add(newFile);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return files;
    }
}

