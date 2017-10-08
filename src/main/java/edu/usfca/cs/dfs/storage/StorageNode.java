package edu.usfca.cs.dfs.storage;

import edu.usfca.cs.dfs.client.ClientProtoBuf;

import java.net.InetAddress;
import java.net.ServerSocket;
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
//        StorageNode server = new StorageNode(9990);
//        StorageNodeHelper shelper = new StorageNodeHelper();
//        server.start();
        System.out.println("Starting storage node on " + hostname + "...");
//        server.heartbeat();
        StorageNodeHelper shelper = new StorageNodeHelper();
        shelper.processClientRequest(9992);
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
