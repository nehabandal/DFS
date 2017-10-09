package edu.usfca.cs.dfs.storage;

import com.google.protobuf.ByteString;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedHashMap;

/**
 * Created by npbandal on 10/7/17.
 */
public class StorageNodeHelper {
    int count = 0;

    LinkedHashMap<Integer, byte[]> chunkIdData = new LinkedHashMap<>();

    public void processClientWriteRequest(StorageProtobuf.StorageMessagePB recfilechunks)
            throws IOException {
        if (recfilechunks.hasStoreChunkMsg()) {

            StorageProtobuf.StoreChunk storeChunkMsg = recfilechunks.getStoreChunkMsg();
            String storeChunkName = recfilechunks.getStoreChunkMsgOrBuilder().getWritefilechunkName();
            int chunkID = recfilechunks.getStoreChunkMsgOrBuilder().getChunkId();
//            System.out.println(storeChunkName); //ChunkName
//            System.out.println(chunkID); //chunkID

            String chunkNameToStore = storeChunkName + chunkID;
//            System.out.println(chunkNameToStore);

            StorageProtobuf.Profile.Builder profile = StorageProtobuf.Profile.newBuilder()
                    .setChunkdatat(storeChunkMsg.getWritechunkdata());

            FileOutputStream output = new FileOutputStream(chunkNameToStore);
            profile.build().writeTo(output);
        }
    }

    public void processClientReadRequest(Socket clientSocket, StorageProtobuf.StorageMessagePB recfilechunks) throws IOException {
        System.out.println("req rec");
        int chunkID = 0;
        String fileName = null;
        byte[] chunkfilecontents = null;
        if (recfilechunks.hasRetrieveChunkFileMsg()) {
            chunkID = recfilechunks.getRetrieveChunkFileMsgOrBuilder().getChunkId();
            fileName = recfilechunks.getRetrieveChunkFileMsgOrBuilder().getReadfileName();


            String chunkName = fileName + chunkID;
            String path = findFile(chunkName, new File("/Users/npbandal/BigData/p1-nehabandal"));
            String chunkfiletoread = path + "/" + chunkName;

            chunkfilecontents = readChunkFromPath(chunkfiletoread);

            String s = new String(chunkfilecontents);
            ByteString data = ByteString.copyFromUtf8(s);
            System.out.println("File content: " + chunkName + ":" + s);


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

