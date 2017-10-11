package edu.usfca.cs.dfs.controller;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by npbandal on 10/9/17.
 */

public class ControllerHeartBeat implements Runnable {
    String hostName;


    public static void main(String[] args) {
        try {

            ServerSocket srvSocket = new ServerSocket(8080);
            Socket clientSocket = srvSocket.accept();
            ProtoHeartbeat.ControllerMessagePB msgWrapper = ProtoHeartbeat.ControllerMessagePB
                    .parseDelimitedFrom(clientSocket.getInputStream());
            if (msgWrapper.hasStorageHeartBeat()) {
                String hostname = msgWrapper.getStorageHeartBeatOrBuilder().getHostName();
                Thread host = new Thread(new ControllerHeartBeat(hostname));
                host.setName(hostname);
                host.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ControllerHeartBeat(String hostname)
    {
        this.hostName = hostname;
    }

    public void run() {
        while (send(hostName))
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
    }

    public synchronized boolean send(String hostName) {

        List<String> allhostnames = new ArrayList<String>();
        allhostnames.add(hostName);

        if (allhostnames.contains(hostName)) {
            System.out.println("Alive! (" + hostName + ")");
            notifyAll();
        } else {
            try {
                long t1 = System.currentTimeMillis();
                wait(2500);
                if ((System.currentTimeMillis() - t1) > 2500) {
                    System.out.println(hostName+ " is dead");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();

            }
        }
        return true;
    }
}
//    public ControllerHeartBeat(String hostname) throws IOException {
////            heartBeat = heartbeat;
//        hostName = hostname;
//    }

//        Thread currentHost = Thread.currentThread();

// you have to collect list of hostnames from gethostname and compare your by checkin  wheather it's in gethostname list
// if contains in list then send alive mesg else send dead node
