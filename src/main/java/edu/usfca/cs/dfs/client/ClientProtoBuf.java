package edu.usfca.cs.dfs.client;

import com.google.protobuf.ByteString;
import edu.usfca.cs.dfs.storage.StorageProtobuf;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
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


    public void protoBufToWriteintoStorageNode(String hostname, int portnumber, String filename, byte[] chunk) {
        Socket sockController = null;
        try {
            String s = new String(chunk);
            ByteString data = ByteString.copyFromUtf8(s);
            // localhost:9990
            // 12345 ---- localhost:9990
            // 23485 ---------^
            sockController = new Socket(hostname, portnumber);
            StorageProtobuf.StoreChunk storeChunkMsg
                    = StorageProtobuf.StoreChunk.newBuilder()
                    .setWritefilechunkName(filename)
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
        finally {
            try {
                sockController.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public void protoBufToSendReadDataToStorageNode(String hostname, int portnumber, String filename, int chunkID) {
        try {
            Socket sockController = new Socket(hostname, portnumber);
            StorageProtobuf.RetrieveFile retrieveFile
                    = StorageProtobuf.RetrieveFile.newBuilder()
                    .setReadfileName(filename)
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

    public void receiveChunkdataFromStorageNode(int portnumber) {
        try {
            ServerSocket srvSocket = new ServerSocket(portnumber);
            Socket clientSocket = srvSocket.accept();
            StorageProtobuf.StorageMessagePB recfilechunks = StorageProtobuf.StorageMessagePB.parseDelimitedFrom(clientSocket.getInputStream());
            if (recfilechunks.hasRetrieveChunkFileMsg()) {
                StorageProtobuf.RetrieveFile retrivechunkfiledata = recfilechunks.getRetrieveChunkFileMsg();
                ByteString chunkdata = retrivechunkfiledata.getReadchunkdata();

//                byte[] chunkbytes = chunkdata.toByteArray();
//                System.out.println(new String(chunkbytes));

                StorageProtobuf.Profile.Builder profile = StorageProtobuf.Profile.newBuilder()
                        .setChunkdatat(chunkdata);

                FileOutputStream output = new FileOutputStream("ThanksGanesha");
                profile.build().writeTo(output);
            }
        } catch (Exception e) {
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




