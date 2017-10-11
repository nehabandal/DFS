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

// collect list of hostnames from gethostname and compare your by checkin  wheather it's in gethostname list
// if contains in list then send alive mesg else send dead node
