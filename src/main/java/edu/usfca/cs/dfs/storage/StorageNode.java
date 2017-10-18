package edu.usfca.cs.dfs.storage;


import edu.usfca.cs.dfs.controller.Heartbeat;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class StorageNode {


    public static void main(String[] args)
            throws Exception {
        final StorageNode storageNode = new StorageNode();
        storageNode.launchThreadStorage(args);

    }

    private void launchThreadStorage(String[] args) throws UnknownHostException, InterruptedException {

        final String hostname = getHostname();
        System.out.println("Storage Node Sending heartbeats on port 8080...");
        final String controllerName = args[0];
//        final int portnumber = Integer.parseInt(args[1]);

        final HashMap<String, Integer> hostnameport = new LinkedHashMap<>();
        hostnameport.put("bass01", 9500);
        hostnameport.put("bass02", 9600);
        hostnameport.put("bass04", 9700);
        hostnameport.put("bass05", 9800);
        hostnameport.put("bass06", 9400);
        hostnameport.put("bass07", 9300);
        hostnameport.put("bass08", 9200);
        hostnameport.put("bass09", 9100);


//        Thread thread1 = threadHeartBeat(controllerName, hostnameport, portnumber);
        Thread thread1 = threadHeartBeat(controllerName, hostname);

        System.out.println("Storage Node processing client request on " + hostname + "...");
        Thread thread2 = threadClientReq();

        Thread thread3 = threadReplicaReq1();

        Thread thread4 = threadReplicaReq2();

        thread1.start();
        thread2.start();
        thread3.start();
        thread4.start();

        thread1.join();
        thread2.join();
        thread3.join();
        thread4.join();
    }

    private Thread threadClientReq() {
        return new Thread() {
            public void run() {
                StorageNodeHelper sh = new StorageNodeHelper();
                ServerSocket srvSocket = null;
                try {
                    srvSocket = new ServerSocket(9901);
                    sh.clientRequests(srvSocket);
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    //
    private Thread threadReplicaReq1() {
        return new Thread() {
            public void run() {
                StorageNodeHelper sh = new StorageNodeHelper();
                ServerSocket srvSocket = null;
                try {
                    srvSocket = new ServerSocket(9910);
                    sh.clientRequests(srvSocket);
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
    }


    private Thread threadReplicaReq2() {
        return new Thread() {
            public void run() {
                StorageNodeHelper sh = new StorageNodeHelper();
                ServerSocket srvSocket = null;
                try {
                    srvSocket = new ServerSocket(9911);
                    sh.clientRequests(srvSocket);
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    //    private Thread threadHeartBeat(final String controllerName, final HashMap<String, Integer> hostdetails, final int portnumber) {
    private Thread threadHeartBeat(final String controllerName, final String hostdetails) {
        return new Thread() {
            public void run() {
                new Heartbeat().send(controllerName, hostdetails, 8080);
            }
        };
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
