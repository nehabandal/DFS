package edu.usfca.cs.dfs.storage;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by npbandal on 10/7/17.
 */
public class StorageNodeHelper {

    LinkedHashMap<Integer, byte[]> chunkIdData = new LinkedHashMap<>();

    public void processClientRequest(int portnumber)
            throws IOException {
        ServerSocket srvSocket = new ServerSocket(portnumber);
        Socket clientSocket = srvSocket.accept();

        StorageProtobuf.StorageMessagePB recfilechunks = StorageProtobuf.StorageMessagePB.parseDelimitedFrom(clientSocket.getInputStream());
        String reqWrite = recfilechunks.getStoreChunkMsgOrBuilder().getReqtypewrite();
        String reqRead = recfilechunks.getRetrieveChunkFileMsgOrBuilder().getReqtyperead();
        if (reqWrite.equals("write")) {
            if (recfilechunks.hasStoreChunkMsg()) {
                StorageProtobuf.StoreChunk storeChunkMsg = recfilechunks.getStoreChunkMsg();
                String storeChunkName = recfilechunks.getStoreChunkMsgOrBuilder().getWritefilechunkName();
                int chunkID = recfilechunks.getStoreChunkMsgOrBuilder().getChunkId();
                String hostName = recfilechunks.getStoreChunkMsgOrBuilder().getHostName();
                Integer chunkIDobj = chunkID;
                System.out.println(storeChunkName); //ChunkName
                System.out.println(chunkID); //chunkID
//            System.out.println(hostName); //hostname

                byte[] bytes = storeChunkMsg.toByteArray();
                String s = new String(bytes);
                System.out.println("File is" + s);

                chunkIdData.put(chunkIDobj, bytes);
            }
        } else if (reqRead.equals("read")) {
            int chunkID = 0;
            if (recfilechunks.hasRetrieveChunkFileMsg()) {
                chunkID = recfilechunks.getRetrieveChunkFileMsgOrBuilder().getChunkId();
                System.out.println(chunkID);
            }
            System.out.println(chunkIdData.size());
            for (Map.Entry<Integer, byte[]> entry : chunkIdData.entrySet()) {
                Integer id = entry.getKey();
                System.out.println(id);
            }

        }
    }
//
//    public void processClientReadRequest(int portnumber) throws IOException {
//        ServerSocket srvSocket = new ServerSocket(portnumber);
//        Socket clientSocket = srvSocket.accept();
//        int chunkID = 0;
//        StorageProtobuf.StorageMessagePB sendFilechunks = StorageProtobuf.StorageMessagePB.parseDelimitedFrom(clientSocket.getInputStream());
//        if (sendFilechunks.hasRetrieveChunkFileMsg()) {
//            chunkID = sendFilechunks.getRetrieveChunkFileMsgOrBuilder().getChunkId();
//        }
//
//        System.out.println(chunkIdData.size());
//        for (Map.Entry<Integer, byte[]> entry : chunkIdData.entrySet()) {
//            Integer id = entry.getKey();
//            System.out.println(id);
//        }

//    }
}

