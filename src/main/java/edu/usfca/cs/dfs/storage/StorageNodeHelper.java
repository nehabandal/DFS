package edu.usfca.cs.dfs.storage;

import com.google.protobuf.ByteString;
import edu.usfca.cs.dfs.client.ClientProtoBuf;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

/**
 * Created by npbandal on 10/7/17.
 */
public class StorageNodeHelper {

    public void clientRequests(ServerSocket srvSocket) throws IOException, InterruptedException {

        while (true) {
            Socket clientSocket = srvSocket.accept();
            StorageProtobuf.StorageMessagePB recfilechunks =
                    StorageProtobuf.StorageMessagePB.parseDelimitedFrom(clientSocket.getInputStream());
            String reqTypeWrite = recfilechunks.getStoreChunkMsgOrBuilder().getReqTypeWrite();
            String reqTypeRead = recfilechunks.getRetrieveChunkFileMsgOrBuilder().getReqTypeRead();

            if (reqTypeWrite.equals("write")) {

                ClientProtoBuf clientProtoBuf = new ClientProtoBuf();
                String storeChunkName = recfilechunks.getStoreChunkMsgOrBuilder().getWritefilechunkName();
                int chunkID = recfilechunks.getStoreChunkMsgOrBuilder().getChunkId();
                HashMap<StorageProtobuf.StoreChunk, List<String>> hostReplica = processClientWriteRequest(recfilechunks);
                for (Map.Entry<StorageProtobuf.StoreChunk, List<String>> entry : hostReplica.entrySet()) {
                    StorageProtobuf.StoreChunk replicaChunk = entry.getKey();
                    List<String> replicaName = entry.getValue();
                    if (replicaName.size() == 2) {
                        callReplica1(replicaName, replicaChunk, storeChunkName, chunkID, clientProtoBuf);
                    }
                }
            }

            if (reqTypeRead.equals("read")) {
                processClientReadRequest(clientSocket, recfilechunks);
            }

        }
    }

    public void clientRequestReplica1(ServerSocket srvSocket) throws IOException, InterruptedException {

        while (true) {
            Socket clientSocket = srvSocket.accept();
            ClientProtoBuf clientProtoBuf = new ClientProtoBuf();
            StorageProtobuf.StorageMessagePB recfilechunks =
                    StorageProtobuf.StorageMessagePB.parseDelimitedFrom(clientSocket.getInputStream());
            String storeChunkName = recfilechunks.getStoreChunkMsgOrBuilder().getWritefilechunkName();
            int chunkID = recfilechunks.getStoreChunkMsgOrBuilder().getChunkId();
            HashMap<StorageProtobuf.StoreChunk, List<String>> hostReplica = processClientWriteRequest(recfilechunks);
            for (Map.Entry<StorageProtobuf.StoreChunk, List<String>> entry : hostReplica.entrySet()) {
                StorageProtobuf.StoreChunk replicaChunk = entry.getKey();
                List<String> replicaName = entry.getValue();
                if (replicaName.size() == 1) {
                    callReplica2(replicaName, replicaChunk, storeChunkName, chunkID, clientProtoBuf);
                }

            }
        }
    }

    public void clientRequestReplica2(ServerSocket srvSocket) throws IOException, InterruptedException {
        while (true) {
            Socket clientSocket = srvSocket.accept();
            StorageProtobuf.StorageMessagePB recfilechunks =
                    StorageProtobuf.StorageMessagePB.parseDelimitedFrom(clientSocket.getInputStream());
            HashMap<StorageProtobuf.StoreChunk, List<String>> hostReplica = processClientWriteRequest(recfilechunks);
        }
    }
//
    private void callReplica1(List<String> hostReplica, StorageProtobuf.StoreChunk storeChunkMsg, String storeChunkName, int chunkID, ClientProtoBuf clientProtoBuf) {
        List<String> hostReplica1 = new ArrayList<>();
        hostReplica1.add(hostReplica.get(1));
        System.out.println("Writing into: " + hostReplica.get(0));
        clientProtoBuf.protoBufToWriteintoStorageNode(hostReplica.get(0), 9910, storeChunkName, chunkID, storeChunkMsg.toByteArray(), hostReplica1);
    }

    private void callReplica2(List<String> hostReplica, StorageProtobuf.StoreChunk storeChunkMsg, String storeChunkName, int chunkID, ClientProtoBuf clientProtoBuf) {
        List<String> hostReplica2 = new ArrayList<>();
        System.out.println("Writing into: " + hostReplica.get(0));
        clientProtoBuf.protoBufToWriteintoStorageNode(hostReplica.get(0), 9911, storeChunkName, chunkID, storeChunkMsg.toByteArray(), hostReplica2);
    }

    public HashMap<StorageProtobuf.StoreChunk, List<String>> processClientWriteRequest(StorageProtobuf.StorageMessagePB recfilechunks)
            throws IOException, InterruptedException {
        HashMap<StorageProtobuf.StoreChunk, List<String>> chunkDataReplica = new LinkedHashMap<>();
        if (recfilechunks.hasStoreChunkMsg()) {

            StorageProtobuf.StoreChunk storeChunkMsg = recfilechunks.getStoreChunkMsg();
            List<String> hostReplica = recfilechunks.getStoreChunkMsgOrBuilder().getHostReplicaList();
            String storeChunkName = recfilechunks.getStoreChunkMsgOrBuilder().getWritefilechunkName();
            int chunkID = recfilechunks.getStoreChunkMsgOrBuilder().getChunkId();
            String chunkNameToStore = storeChunkName + "_" + chunkID;

            //Writing into files
            StorageProtobuf.Profile.Builder profile = StorageProtobuf.Profile.newBuilder()
                    .setChunkdatat(storeChunkMsg.getWritechunkdata());

            FileOutputStream output = new FileOutputStream(chunkNameToStore);
            profile.build().writeTo(output);
            chunkDataReplica.put(storeChunkMsg, hostReplica);

        }
        return chunkDataReplica;
    }

    public void processClientReadRequest(Socket clientSocket, StorageProtobuf.StorageMessagePB recfilechunks) throws IOException {
        String chunkName = null;
        byte[] chunkfilecontents = null;
        if (recfilechunks.hasRetrieveChunkFileMsg()) {

            chunkName = recfilechunks.getRetrieveChunkFileMsgOrBuilder().getReadfileName();

            String path = findFile(chunkName, new File("/home2/npbandal"));
            String chunkfiletoread = path + "/" + chunkName;

            chunkfilecontents = readChunkFromPath(chunkfiletoread);

            String s = new String(chunkfilecontents);
            ByteString data = ByteString.copyFromUtf8(s);
            System.out.println("File content: " + chunkName + ": ");
            System.out.println(s);

            //Sending chunk data to client
            StorageProtobuf.RetrieveFile retrieveFile
                    = StorageProtobuf.RetrieveFile.newBuilder()
                    .setReadchunkdata(data)
                    .build();
            StorageProtobuf.StorageMessagePB msgWrapper =
                    StorageProtobuf.StorageMessagePB.newBuilder()
                            .setRetrieveChunkFileMsg(retrieveFile)
                            .build();
            msgWrapper.writeDelimitedTo(clientSocket.getOutputStream());
        }
    }

    public byte[] readChunkFromPath(String path) {
        File file = new File(path);
        FileInputStream fin = null;
        byte fileContent[] = new byte[(int) file.length()];
        try {
            fin = new FileInputStream(file);
            fin.read(fileContent);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fin != null)
                    fin.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return fileContent;

    }

    public String findFile(String name, File file) {
        File[] list = file.listFiles();
        String pathNname = null;
        if (list != null)
            for (File actualFile : list) {
                if (actualFile.isDirectory()) {
                    findFile(name, actualFile);
                } else if (name.equalsIgnoreCase(actualFile.getName())) {
                    pathNname = String.valueOf(actualFile.getParentFile());
                }
            }
        return pathNname;
    }

}

