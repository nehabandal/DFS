package edu.usfca.cs.dfs.controller;

import java.io.File;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

    public synchronized boolean send(String controllerhost, String hostname, int portnum) {

        try {
            while (hostname != null) { //should be not equal in actual code
                long space = new File("/").getFreeSpace();
                Socket sockController = new Socket(controllerhost, portnum);
                ProtoHeartbeat.StorageHearbeat heartbeat
                        = ProtoHeartbeat.StorageHearbeat.newBuilder()
                        .setHostName(hostname)
                        .setFreespace((int) space)
                        .setHeartbeatmsg("Hi from ")

                        .build();
                ProtoHeartbeat.ControllerMessagePB msgWrapper =
                        ProtoHeartbeat.ControllerMessagePB.newBuilder()
                                .setStorageHeartBeat(heartbeat)
                                .build();

                msgWrapper.writeDelimitedTo(sockController.getOutputStream());
                Thread.sleep(3000);
            }
        } catch (Exception e) {
        }
        try {
            long t1 = System.currentTimeMillis();
            wait(5000);
            if ((System.currentTimeMillis() - t1) > 5000) {
                System.out.println(hostname + " is dead");
            }
        } catch (InterruptedException e) {
        }
        return true;
    }


    public HashMap<String, Integer> receive(ServerSocket srvSocket) {
        String hostname = null;
        String msg = null;
        HashMap<String, Integer> hostNameSize = new HashMap<>();
        List<String> hosta = new ArrayList<>();

        int freespace = 0;
        try {
            while (true) {
                Socket clientSocket = srvSocket.accept();
                ProtoHeartbeat.ControllerMessagePB msgWrapper = ProtoHeartbeat.ControllerMessagePB
                        .parseDelimitedFrom(clientSocket.getInputStream());
                if (msgWrapper.hasStorageHeartBeat()) {
                    hostname = msgWrapper.getStorageHeartBeatOrBuilder().getHostName();
                    msg = msgWrapper.getStorageHeartBeatOrBuilder().getHeartbeatmsg();
                    freespace = msgWrapper.getStorageHeartBeatOrBuilder().getFreespace();

                    System.out.println(msg + "Host: " + hostname + "Available size: " + freespace);

                    hostNameSize.put(hostname, freespace);

                    if (hostNameSize.size() == 3) {
                        return hostNameSize;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hostNameSize;
    }
}
