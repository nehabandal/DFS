package edu.usfca.cs.dfs.controller;

import java.io.File;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by npbandal on 10/7/17.
 */
public class Heartbeat implements Runnable {

    private String controllerhost;
    private String hostName;
    private HashMap<String, Integer> hostdetails;
    private int portNum;
    int count = 0;

    public Heartbeat() {

    }

    @Override
    public void run() {
        while (send(controllerhost, hostdetails, portNum))
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
    }

    public synchronized boolean send(String controllerhost, HashMap<String, Integer> hostname, int portnum) {
//    public synchronized boolean send(String controllerhost, String hostname, int portnum) {

        File directory = new File("/");
//        File directory = new File("/home2/npbandal/");
        long space = directory.getFreeSpace();
        long spaceMB = space / (1024 * 1024);
        File[] fList = directory.listFiles();
        List<String> filesinHost = new ArrayList<>();
        if (fList != null) {
            for (File fileinhost : fList) {
                filesinHost.add(fileinhost.getName());
            }
        } else {
            filesinHost.add("No file found");
        }

        try {
            while (hostname != null) { //should be not equal in actual code
                for (String host : hostname.keySet()) {
                    Socket sockController = new Socket(controllerhost, portnum);
                    ProtoHeartbeat.StorageHearbeat heartbeat
                            = ProtoHeartbeat.StorageHearbeat.newBuilder()
                            .setHostName(host)
                            .setFreespace(spaceMB)
                            .addAllFileName(filesinHost)
                            .setHeartbeatmsg("Hi from ")
                            .build();
                    ProtoHeartbeat.ControllerMessagePB msgWrapper =
                            ProtoHeartbeat.ControllerMessagePB.newBuilder()
                                    .setStorageHeartBeat(heartbeat)
                                    .build();
                    msgWrapper.writeDelimitedTo(sockController.getOutputStream());
                    Thread.sleep(3000);
                }
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

    public Map<String, Map<Long, List<String>>> receive(ServerSocket srvSocket) {
        String msg = null;
        Map<String, Map<Long, List<String>>> hostNameSpaceFiles = new HashMap<>();
        List<String> filenames = null;
        HashMap<Long, List<String>> hostSpceFiles = new HashMap<>();

        try {
            Socket clientSocket = srvSocket.accept();

            ProtoHeartbeat.ControllerMessagePB msgWrapper = ProtoHeartbeat.ControllerMessagePB
                    .parseDelimitedFrom(clientSocket.getInputStream());
            if (msgWrapper.hasStorageHeartBeat()) {
                String hostname = msgWrapper.getStorageHeartBeatOrBuilder().getHostName();
                msg = msgWrapper.getStorageHeartBeatOrBuilder().getHeartbeatmsg();
                Long freespace = msgWrapper.getStorageHeartBeatOrBuilder().getFreespace();
                filenames = msgWrapper.getStorageHeartBeatOrBuilder().getFileNameList();
                System.out.println(msg + "Host: " + hostname + " Available size: " + freespace + " MB");
//                for (String filename : filenames) {
//                    System.out.println(filename);
//                }
                hostSpceFiles.put(freespace, filenames);
                hostNameSpaceFiles.put(hostname, hostSpceFiles);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hostNameSpaceFiles;
    }
}
