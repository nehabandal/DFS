package edu.usfca.cs.dfs;

import com.google.protobuf.ByteString;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by npbandal on 10/1/17.
 */
public class ProtoBuf implements ChunkHelper {

    public void protoBufToSendReqToControllerFromClient(int portnumber, String chunkname) {
        protoBufToSendReq(portnumber, chunkname);
    }

    public void protoBufToReceiveRequestFromClientAtController(int portnumber, String msg) throws IOException {
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

    public void protoBufToSendResponseToClientFromController(int portnumber, List<String> hostnames) {
        for (String hostname : hostnames) {
            protoBufToSendReq(portnumber, hostname);
        }
    }

    public List<String> protoBufToReceiveResponseFromControllerAtClientSide(int portnumber) throws IOException {
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

    public void protoBufToSendWriteReqToStorageNodeFromClient(String hostname, int portnumber, byte[] chunk) {
        try {

            ByteString data = ByteString.copyFrom(chunk);

            Socket sockController = new Socket(hostname, portnumber);
            StorageMessages.StoreChunk storeChunkMsg
                    = StorageMessages.StoreChunk.newBuilder()
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
                byte[] bytes = storeChunkMsg.toByteArray();
                String s = new String(bytes);
                System.out.println(msg + "\n" + s);
            }
        } finally {
            srvSocket.close();
        }
    }

    public void protoBufToSendHeartbeatFromStorageNodeToController(int portnumber, String msg) {
        try {

//            ByteString data = ByteString.copyFrom(chunk);
            Socket sockController = new Socket("localhost", portnumber);
            StorageMessages.StoreChunk storeChunkMsg
                    = StorageMessages.StoreChunk.newBuilder()
                    .setFileName(msg)
                    .build();
            StorageMessages.StorageMessageWrapper msgWrapper =
                    StorageMessages.StorageMessageWrapper.newBuilder()
                            .setStoreChunkMsg(storeChunkMsg)
                            .build();
            msgWrapper.writeDelimitedTo(sockController.getOutputStream());
            sockController.close();
        } catch (IOException e) {

        }

    }

    public void protoBufToReceiveHeartbeatFromStorageNodeAtController(int portnumber) throws IOException {
        ServerSocket srvSocket = new ServerSocket(portnumber);
        try {
            Socket client = srvSocket.accept();
            StorageMessages.StorageMessageWrapper msgWrapper
                    = StorageMessages.StorageMessageWrapper.parseDelimitedFrom(
                    client.getInputStream());
            if (msgWrapper.hasStoreChunkMsg()) {
                StorageMessages.StoreChunk storeChunkMsg
                        = msgWrapper.getStoreChunkMsg();
                System.out.println("Host Alive " + storeChunkMsg.getFileName());
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
    public List<byte[]> splitFile(File file) {
        final int CHUNK_SIZE = 1024 * 1024;
        final int nChunks = (int) Math.ceil((double) file.length() / CHUNK_SIZE);
        List<byte[]> fileInChunks = new ArrayList<>();
        try {
            final FileInputStream fis = new FileInputStream(file);
            for (int i = 0; i < nChunks; ++i) {
                final byte[] bytes = new byte[CHUNK_SIZE];
                fis.read(bytes);
                String emptyRemoved = new String(bytes).replaceAll("\u0000.*", "");
                byte[] finalarray = emptyRemoved.getBytes();
                fileInChunks.add(finalarray);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileInChunks;
    }
}

