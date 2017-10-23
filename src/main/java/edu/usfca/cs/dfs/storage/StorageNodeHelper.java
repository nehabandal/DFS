package edu.usfca.cs.dfs.storage;

import com.google.protobuf.ByteString;
import edu.usfca.cs.dfs.client.ClientProtoBuf;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

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
                StorageProtobuf.StoreChunk storeChunkMsg = recfilechunks.getStoreChunkMsg();

                List<String> hostReplica = processClientWriteRequest(recfilechunks, storeChunkMsg, chunkID, storeChunkName);

                List<String> hostReplica1 = new ArrayList<>();
                if (hostReplica.size() == 2) {
                    hostReplica1.add(hostReplica.get(1));
                    System.out.println("Writing into: " + hostReplica.get(0));
                    clientProtoBuf.protoBufToWriteintoStorageNode(hostReplica.get(0), 13003, storeChunkName, chunkID, storeChunkMsg.getWritechunkdata().toByteArray(), hostReplica1);
                }
                if (hostReplica.size() == 1) {
                    List<String> hostReplica2 = new ArrayList<>();
                    System.out.println("Writing into: " + hostReplica.get(0));
                    clientProtoBuf.protoBufToWriteintoStorageNode(hostReplica.get(0), 13004, storeChunkName, chunkID, storeChunkMsg.getWritechunkdata().toByteArray(), hostReplica2);
                }

            }

            if (reqTypeRead.equals("read")) {
                processClientReadRequest(clientSocket, recfilechunks);
            }

        }

    }

    public List<String> processClientWriteRequest(StorageProtobuf.StorageMessagePB recfilechunks, StorageProtobuf.StoreChunk storeChunkMsg, int chunkID, String storeChunkName)
            throws IOException, InterruptedException {
        List<String> hostReplica = new ArrayList<>();
        String chunkNameToStore;
        if (recfilechunks.hasStoreChunkMsg()) {

            hostReplica = recfilechunks.getStoreChunkMsgOrBuilder().getHostReplicaList();
            if (chunkID == 0)
                chunkNameToStore = storeChunkName;
            else
                chunkNameToStore = storeChunkName + "_" + chunkID;

            //Writing chunk data into files
            try (FileOutputStream fop = new FileOutputStream(chunkNameToStore)) {
                fop.write(storeChunkMsg.getWritechunkdata().toByteArray());
                fop.flush();
                fop.close();
            } catch (Exception e) {
                System.out.println("No file written");
            }

            //Writing checksum data
            String md5 = DigestUtils.md5Hex(storeChunkMsg.getWritechunkdata().toByteArray());
            if (chunkID != 0) {
                System.out.println("Checksum: " + md5);
                try (FileOutputStream fop = new FileOutputStream("storage_checksum_" + chunkNameToStore)) {
                    fop.write(md5.getBytes());
                    fop.flush();
                    fop.close();
                } catch (Exception e) {
                    System.out.println("No file written");
                }
            }

        }

        return hostReplica;
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

            //creating checksum
            String md5 = DigestUtils.md5Hex(s);

            //Sending chunk data to client
            StorageProtobuf.RetrieveFile retrieveFile
                    = StorageProtobuf.RetrieveFile.newBuilder()
                    .setReadchunkdata(data)
                    .setChecksum(md5)
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

