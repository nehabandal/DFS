package edu.usfca.cs.dfs.controller;

import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by npbandal on 10/7/17.
 */
public class Heartbeat implements Runnable {

    private String controllerhost;
    private String hostName;
    private int portNum;

    public Heartbeat(String ControllerName, String hostname, int portnum) {
        controllerhost = ControllerName;
        hostName = hostname;
        portNum = portnum;

    }

    @Override
    public void run() {
        while (send(controllerhost, hostName, 8080))
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
    }

    public synchronized boolean send(String controllerhost, String hostname, int portnum) {

        try {
            while (hostname != null) { //should be not equal in actual code
                Socket sockController = new Socket(controllerhost, portnum);
                ProtoHeartbeat.StorageHearbeat heartbeat
                        = ProtoHeartbeat.StorageHearbeat.newBuilder()
                        .setHostName(hostname)
                        .setHeartbeatmsg("Hi from ")

                        .build();
                ProtoHeartbeat.ControllerMessagePB msgWrapper =
                        ProtoHeartbeat.ControllerMessagePB.newBuilder()
                                .setStorageHeartBeat(heartbeat)
                                .build();

                msgWrapper.writeDelimitedTo(sockController.getOutputStream());
            }
            Thread.sleep(100);
        } catch (Exception e) {
        }
        try {
            long t1 = System.currentTimeMillis();
            wait(3000);
            if ((System.currentTimeMillis() - t1) > 3000) {
                System.out.println(hostname + "is dead");
            }
        } catch (InterruptedException e) {
        }
        return true;
    }

}
