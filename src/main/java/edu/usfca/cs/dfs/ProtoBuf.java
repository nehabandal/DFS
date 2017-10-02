package edu.usfca.cs.dfs;

import com.google.protobuf.ByteString;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by npbandal on 10/1/17.
 */
public class ProtoBuf implements ChunkHelper {

    public void protoBufToSendReqToController(int portnumber, String chunkname) {
        protoBufToSendReq(portnumber, chunkname);
    }

    public void protoBufToReceiveRequestFromClient(int portnumber, String msg) throws IOException {
        ServerSocket srvSocket = new ServerSocket(portnumber);
        try {
            Socket client = srvSocket.accept();
            StorageMessages.StorageMessageWrapper msgWrapper
                    = StorageMessages.StorageMessageWrapper.parseDelimitedFrom(
                    client.getInputStream());
            if (msgWrapper.hasStoreChunkMsg()) {
                StorageMessages.StoreChunk storeChunkMsg
                        = msgWrapper.getStoreChunkMsg();
                System.out.println(msg + storeChunkMsg.getFileName());
            }
        } finally {
            srvSocket.close();
        }
    }

    public void protoBufToSendResponseToClient(int portnumber, List<String> hostnames) {
        for (String hostname : hostnames) {
            protoBufToSendReq(portnumber, hostname);
        }
    }

    public List<String> protoBufToReceiveResponseFromController(int portnumber) throws IOException {
        ServerSocket srvSocket = new ServerSocket(portnumber);
        List<String> hostnames = new ArrayList<>();
        int i = 0;
        try {
            while (i < 3) {
                Socket client = srvSocket.accept();
                StorageMessages.StorageMessageWrapper msgWrapper
                        = StorageMessages.StorageMessageWrapper.parseDelimitedFrom(
                        client.getInputStream());
                if (msgWrapper.hasStoreChunkMsg()) {
                    StorageMessages.StoreChunk storeChunkMsg
                            = msgWrapper.getStoreChunkMsg();
                    hostnames.add(storeChunkMsg.getFileName());
                }
                i++;
                client.close();
            }
            srvSocket.close();

        } finally {
            srvSocket.close();
        }
        return hostnames;
    }

    public void protoBufToSendWriteReqToStorageNode(String hostname, int portnumber, File chunk) {
        Path path = Paths.get(chunk.getPath());
        try {
            byte[] chunkdata = Files.readAllBytes(path);

            ByteString data = ByteString.copyFrom(chunkdata);

            Socket sockController = new Socket(hostname, portnumber);
            StorageMessages.StoreChunk storeChunkMsg
                    = StorageMessages.StoreChunk.newBuilder()
                    .setFileName(chunk.getName())
                    .setChunkId(3)
                    .setData(data)
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

    public void protoBufToReceiveRequestFromClientAtStorageNode(int portnumber, String msg) throws IOException {
        ServerSocket srvSocket = new ServerSocket(portnumber);
        try {
            Socket client = srvSocket.accept();
            StorageMessages.StorageMessageWrapper msgWrapper
                    = StorageMessages.StorageMessageWrapper.parseDelimitedFrom(
                    client.getInputStream());
            if (msgWrapper.hasStoreChunkMsg()) {
                StorageMessages.StoreChunk storeChunkMsg
                        = msgWrapper.getStoreChunkMsg();
//                System.out.println(msg + storeChunkMsg.getData());
                byte[] bytes = storeChunkMsg.toByteArray();
                String s = new String(bytes);
                System.out.println(msg + "\n" + s);
            }
        } finally {
            srvSocket.close();
        }
    }


    private void protoBufToSendReq(int portnumber, String chunkname) {
        try {
            Socket sockController = new Socket("localhost", portnumber);
            StorageMessages.StoreChunk storeChunkMsg
                    = StorageMessages.StoreChunk.newBuilder()
                    .setFileName(chunkname)
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
                        + String.format("%03d", counter++));
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
        } catch (IOException e) {
            e.printStackTrace();
        }
        return files;
    }

}

