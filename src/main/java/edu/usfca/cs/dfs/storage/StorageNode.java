package edu.usfca.cs.dfs.storage;


import edu.usfca.cs.dfs.controller.Heartbeat;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;

public class StorageNode {


    public static void main(String[] args)
            throws Exception {

        final String hostname = getHostname();
        System.out.println("Starting storage node on " + hostname + "...");
        final String controllerName = args[0];
        final int portnumber = Integer.parseInt(args[1]);

        Thread thread1 = new Thread() {
            public void run() {
                new Heartbeat().send(controllerName, hostname, portnumber);
            }
        };
        Thread thread2 = new Thread() {
            public void run() {
                StorageNodeHelper sh = new StorageNodeHelper();
                ServerSocket srvSocket = null;
                try {
                    srvSocket = new ServerSocket(9901);
                    sh.clientRequests(srvSocket);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };

        thread1.start();
        thread2.start();

        thread1.join();
        thread2.join();

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
