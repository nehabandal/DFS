package edu.usfca.cs.dfs.storage;

import edu.usfca.cs.dfs.controller.Heartbeat;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by npbandal on 10/9/17.
 */
public class StorageNodeHeartBeat {

    public static void main(String[] args) throws UnknownHostException {
        String controllerName = args[0];
        int portnumber = Integer.parseInt(args[1]);
        String hostname = getHostname();
//        System.out.println(hostname);
        while (true) {
            Heartbeat heartbeat = new Heartbeat(controllerName, hostname, portnumber);
            heartbeat.run();
        }

    }

    private static String getHostname() throws UnknownHostException {
        return InetAddress.getLocalHost().getHostName();
    }

}
