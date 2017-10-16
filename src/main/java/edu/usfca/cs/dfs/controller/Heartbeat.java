package edu.usfca.cs.dfs.controller;

import java.io.File;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

/**
 * Created by npbandal on 10/7/17.
 */
public class Heartbeat implements Runnable {

    private String controllerhost;
    private String hostName;
    private HashMap<String, Integer> hostdetails;
    private int portNum;
    int count = 0;


    public Heartbeat(String ControllerName, String hostname, int portnum) {
        controllerhost = ControllerName;
        hostName = hostname;
        portNum = portnum;

    }

    public Heartbeat() {

    }

    @Override
    public void run() {
        while (send(controllerhost, hostName, 8080))
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
    }

    //    public synchronized boolean send(String controllerhost, HashMap<String, Integer> hostname, int portnum) {
    public synchronized boolean send(String controllerhost, String hostname, int portnum) {


        try {
            while (hostname != null) { //should be not equal in actual code
//                for (String host : hostname.keySet()) {

                long space = new File("/").getFreeSpace();
                long spaceMB = space / (1024 * 1024);

                Socket sockController = new Socket(controllerhost, portnum);
                ProtoHeartbeat.StorageHearbeat heartbeat
                        = ProtoHeartbeat.StorageHearbeat.newBuilder()
                        .setHostName(hostname)
                        .setFreespace(spaceMB)
                        .setHeartbeatmsg("Hi from ")
                        .build();
                ProtoHeartbeat.ControllerMessagePB msgWrapper =
                        ProtoHeartbeat.ControllerMessagePB.newBuilder()
                                .setStorageHeartBeat(heartbeat)
                                .build();

                msgWrapper.writeDelimitedTo(sockController.getOutputStream());
                Thread.sleep(3000);
//                }
            }
        } catch (Exception e) {
            try {
                long t1 = System.currentTimeMillis();
                wait(5000);
                if ((System.currentTimeMillis() - t1) > 5000) {
                    System.out.println(controllerhost + " is dead");
                }
            } catch (InterruptedException e1) {
                e.printStackTrace();
            }
        }

        return true;
    }


    public HashMap<String, Long> receive(ServerSocket srvSocket) {
        String msg = null;
        HashMap<String, Long> hostNameSpace = new HashMap<>();
        try {
            Socket clientSocket = srvSocket.accept();

            ProtoHeartbeat.ControllerMessagePB msgWrapper = ProtoHeartbeat.ControllerMessagePB
                    .parseDelimitedFrom(clientSocket.getInputStream());
            if (msgWrapper.hasStorageHeartBeat()) {
                String hostname = msgWrapper.getStorageHeartBeatOrBuilder().getHostName();
                msg = msgWrapper.getStorageHeartBeatOrBuilder().getHeartbeatmsg();
                Long freespace = msgWrapper.getStorageHeartBeatOrBuilder().getFreespace();
                System.out.println(msg + "Host: " + hostname + " Available size: " + freespace + " MB");
                if (hostname != null && freespace != 0) {
                    hostNameSpace.put(hostname, freespace);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hostNameSpace;
    }
}
