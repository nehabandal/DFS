package edu.usfca.cs.dfs.storage;


import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class StorageNode {

    static final int PORT = 9995;

    public static void main(String[] args)
            throws Exception {

        String hostname = getHostname();
        System.out.println("Starting storage node on " + hostname + "...");

//        server.heartbeat();

        //To process read or write request from client
        ServerSocket srvSocket = null;
        Socket clientSocket = null;
        try {
            srvSocket = new ServerSocket(PORT);
        } catch (IOException e) {
            e.printStackTrace();

        }
        while (srvSocket != null) {
            try {
                clientSocket = srvSocket.accept();
            } catch (IOException e) {
                System.out.println("I/O error: " + e);
            }
            new WriteThread(clientSocket).start();
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
