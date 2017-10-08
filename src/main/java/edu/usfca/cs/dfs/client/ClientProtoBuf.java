package edu.usfca.cs.dfs.client;

import com.google.protobuf.ByteString;
import edu.usfca.cs.dfs.storage.StorageProtobuf;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by npbandal on 10/1/17.
 */
public class ClientProtoBuf {

//    public void protoBufToReceiveRequestFromClientAtController(int portnumber, String msg) throws IOException {
//        ServerSocket srvSocket = new ServerSocket(portnumber);
//        try {
//            Socket client = srvSocket.accept();
//            StorageProtobuf.StorageMessagePB msgWrapper
//                    = StorageProtobuf.StorageMessagePB.parseDelimitedFrom(
//                    client.getInputStream());
//            if (msgWrapper.hasStoreChunkMsg()) {
//                StorageMessages.StoreChunk storeChunkMsg
//                        = msgWrapper.getStoreChunkMsg();
//                System.out.println(msg + storeChunkMsg.getFileName());
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//


    public void protoBufToWriteintoStorageNode(String hostname, int portnumber, String chunkName, byte[] chunk) {
        try {

            ByteString data = ByteString.copyFrom(chunk);
            // localhost:9990
            // 12345 ---- localhost:9990
            // 23485 ---------^
            Socket sockController = new Socket(hostname, portnumber);
            StorageProtobuf.StoreChunk storeChunkMsg
                    = StorageProtobuf.StoreChunk.newBuilder()
                    .setWritefilechunkName(chunkName)
                    .setChunkId(1)
                    .setWritechunkdata(data)
                    .setReqtypewrite("write")
                    .build();
            StorageProtobuf.StorageMessagePB msgWrapper =
                    StorageProtobuf.StorageMessagePB.newBuilder()
                            .setStoreChunkMsg(storeChunkMsg)
                            .build();
            msgWrapper.writeDelimitedTo(sockController.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void protoBufToReadfromStorageNode(String hostname, int portnumber, int chunkID) {
        try {
            Socket sockController = new Socket(hostname, portnumber);
            StorageProtobuf.RetrieveFile retrieveFile
                    = StorageProtobuf.RetrieveFile.newBuilder()
                    .setChunkId(chunkID)
                    .setReqtyperead("read")
                    .build();
            StorageProtobuf.StorageMessagePB msgWrapper =
                    StorageProtobuf.StorageMessagePB.newBuilder()
                            .setRetrieveChunkFileMsg(retrieveFile)
                            .build();
            msgWrapper.writeDelimitedTo(sockController.getOutputStream());
            sockController.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


//
//    public void protoBufToSendHeartbeatFromStorageNodeToController(int portnumber, String msg) {
//        try {
//
////            ByteString data = ByteString.copyFrom(chunk);
//            Socket sockController = new Socket("localhost", portnumber);
//            StorageMessages.StoreChunk storeChunkMsg
//                    = StorageMessages.StoreChunk.newBuilder()
//                    .setFileName(msg)
//                    .build();
//            StorageMessages.StorageMessageWrapper msgWrapper =
//                    StorageMessages.StorageMessageWrapper.newBuilder()
//                            .setStoreChunkMsg(storeChunkMsg)
//                            .build();
//            msgWrapper.writeDelimitedTo(sockController.getOutputStream());
//            sockController.close();
//        } catch (IOException e) {
//
//        }

//    }

//    public void protoBufToReceiveHeartbeatFromStorageNodeAtController(int portnumber) throws IOException {
//        ServerSocket srvSocket = new ServerSocket(portnumber);
//        try {
//            Socket client = srvSocket.accept();
//            StorageMessages.StorageMessageWrapper msgWrapper
//                    = StorageMessages.StorageMessageWrapper.parseDelimitedFrom(
//                    client.getInputStream());
//            if (msgWrapper.hasStoreChunkMsg()) {
//                StorageMessages.StoreChunk storeChunkMsg
//                        = msgWrapper.getStoreChunkMsg();
//                System.out.println("Host Alive " + storeChunkMsg.getFileName());
//            }
//        } finally {
//            srvSocket.close();
//        }
//    }

//    Controller protobuf
//    public void protoBufToSendReq(int portnumber, String chunkname) {
//        try {
//            Socket sockController = new Socket("localhost", portnumber);
//            StorageMessages.StoreChunk storeChunkMsg
//                    = StorageMessages.StoreChunk.newBuilder()
//                    .setFileName(chunkname)
//                    .build();
//            StorageMessages.StorageMessageWrapper msgWrapper =
//                    StorageMessages.StorageMessageWrapper.newBuilder()
//                            .setStoreChunkMsg(storeChunkMsg)
//                            .build();
//            msgWrapper.writeDelimitedTo(sockController.getOutputStream());
////            sockController.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
}




