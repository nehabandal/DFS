package edu.usfca.cs.dfs.storage;


import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class StorageNode {


    public static void main(String[] args)
            throws Exception {

        String hostname = getHostname();
        System.out.println("Starting storage node on " + hostname + "...");
        StorageNodeHelper sh = new StorageNodeHelper();
//        byte[] chunkdata = null;

//        server.heartbeat();
        ServerSocket srvSocket = new ServerSocket(9001);
        while (true) {
            Socket clientSocket = srvSocket.accept();
            StorageProtobuf.StorageMessagePB recfilechunks =
                    StorageProtobuf.StorageMessagePB.parseDelimitedFrom(clientSocket.getInputStream());
            String reqWrite = recfilechunks.getStoreChunkMsgOrBuilder().getReqtypewrite();
            String reqRead = recfilechunks.getRetrieveChunkFileMsgOrBuilder().getReqtyperead();
            if (reqWrite.equals("write")) {
                System.out.println("hi");
                sh.processClientWriteRequest(recfilechunks);
            }
            if (reqRead.equals("read")) {
                System.out.println("hello");
                sh.processClientReadRequest(clientSocket, recfilechunks);
            }
        }
    }


    /**
     * Retrieves the short host name of the current host.
     *
     * @return name of the current host
     */

    private static String getHostname()
            throws UnknownHostException {
        return InetAddress.getLocalHost().getHostName();
    }

}
