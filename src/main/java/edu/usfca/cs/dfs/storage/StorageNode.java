package edu.usfca.cs.dfs.storage;


import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;

public class StorageNode {


    public static void main(String[] args)
            throws Exception {

        String hostname = getHostname();
        System.out.println("Starting storage node on " + hostname + "...");
        StorageNodeHelper sh = new StorageNodeHelper();

//        server.heartbeat();
        ServerSocket srvSocket = new ServerSocket(9901);
        sh.clientRequests(srvSocket);

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
