package edu.usfca.cs.dfs.controller;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by npbandal on 10/9/17.
 */

public class ControllerHeartBeat {
    String hostName;

    public static void main(String[] args) throws IOException {
        ServerSocket srvSocket = new ServerSocket(8080);

        while (true) {
            Socket clientSocket = srvSocket.accept();
            ProtoHeartbeat.ControllerMessagePB msgWrapper = ProtoHeartbeat.ControllerMessagePB
                    .parseDelimitedFrom(clientSocket.getInputStream());
            if (msgWrapper.hasStorageHeartBeat()) {
                String hostname = msgWrapper.getStorageHeartBeatOrBuilder().getHostName();
                Thread host = new Thread(new Heartbeat(hostname));
                host.setName(hostname);
                host.start();

                if (host.getName() == InetAddress.getLocalHost().getHostName()) {
                    break;
                }
            }
        }
        try {
            // Wait 5 seconds
            Thread.currentThread().sleep(5000);
        } catch (InterruptedException e) {
        }

//        table.hit("DONE"); // cause the players to quit their threads.
        try {
            Thread.currentThread().sleep(100);
        } catch (InterruptedException e) {
        }
    }
}

//    Thread bob = new Thread(new Heartbeat("alice"));
//
//
//        alice.setName("alice");
//                bob.setName("bob");
//                alice.start();    // alice starts playing
//                bob.start();  // bob starts playing


//    public static void main(String[] args) {
//        try {
//
//            ServerSocket srvSocket = new ServerSocket(8080);
//            Socket clientSocket = srvSocket.accept();
//            ProtoHeartbeat.ControllerMessagePB msgWrapper = ProtoHeartbeat.ControllerMessagePB
//                    .parseDelimitedFrom(clientSocket.getInputStream());
//            if (msgWrapper.hasStorageHeartBeat()) {
//                String hostname = msgWrapper.getStorageHeartBeatOrBuilder().getHostName();
//                Thread host = new Thread(new ControllerHeartBeat(hostname));
//                host.setName(hostname);
//                host.start();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    public ControllerHeartBeat(String hostname)
//    {
//        this.hostName = hostname;
//    }
//
//    public void run() {
//        while (send(hostName))
//            try {
//                Thread.sleep(500);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//    }
//
//    public synchronized boolean send(String hostName) {
//
//        List<String> allhostnames = new ArrayList<String>();
//        allhostnames.add(hostName);
//
//        if (allhostnames.contains(hostName)) {
//            System.out.println("Alive! (" + hostName + ")");
//            notifyAll();
//        } else {
//            try {
//                long t1 = System.currentTimeMillis();
//                wait(2500);
//                if ((System.currentTimeMillis() - t1) > 2500) {
//                    System.out.println(hostName+ " is dead");
//                }
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//
//            }
//        }
//        return true;
//    }
//}
//    public ControllerHeartBeat(String hostname) throws IOException {
////            heartBeat = heartbeat;
//        hostName = hostname;
//    }

//        Thread currentHost = Thread.currentThread();

// you have to collect list of hostnames from gethostname and compare your by checkin  wheather it's in gethostname list
// if contains in list then send alive mesg else send dead node
