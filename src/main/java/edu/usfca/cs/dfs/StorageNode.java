package edu.usfca.cs.dfs;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class StorageNode extends ProtoBuf  {

    protected int serverPort = 8080;
    protected ServerSocket serverSocket = null;
    protected boolean isStopped = false;
    protected Thread runningThread = null;


    public static void main(String[] args)
            throws Exception {
        String hostname = getHostname();
        ProtoBuf pb = new ProtoBuf();
        System.out.println("Starting storage node on " + hostname + "...");
        pb.protoBufToReceiveRequestFromClientAtStorageNode(9990, "File received ");
        pb.protoBufToSendReqToControllerFromClient(9000,"Hi storage node ML-ITS-601927 here");

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
