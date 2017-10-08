package edu.usfca.cs.dfs.storage;

import edu.usfca.cs.dfs.client.ClientProtoBuf;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class StorageNode {
    protected Thread runningThread = null;
    protected ServerSocket serverSocket = null;
    protected int serverPort;
    protected boolean isStopped = false;
    private ClientProtoBuf pb = new ClientProtoBuf();

    public static void main(String[] args)
            throws Exception {

        String hostname = getHostname();
        System.out.println("Starting storage node on " + hostname + "...");
        byte[] chunkdata = null;
//        server.heartbeat();
        StorageNodeHelper shelper = new StorageNodeHelper();

        //To process read or write request from client
        ServerSocket srvSocket = new ServerSocket(9992);
        Socket clientSocket = srvSocket.accept();
        StorageProtobuf.StorageMessagePB recfilechunks = StorageProtobuf.StorageMessagePB.parseDelimitedFrom(clientSocket.getInputStream());
        String reqWrite = recfilechunks.getStoreChunkMsgOrBuilder().getReqtypewrite();
        String reqRead = recfilechunks.getRetrieveChunkFileMsgOrBuilder().getReqtyperead();
        if (reqWrite.equals("write")) {
            shelper.processClientWriteRequest(recfilechunks);
        }
        if (reqRead.equals("read")) {
            chunkdata = shelper.recClientChunkDataRequest(recfilechunks);
        }
        shelper.sendChunkDatatoClient(9994, chunkdata);

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
