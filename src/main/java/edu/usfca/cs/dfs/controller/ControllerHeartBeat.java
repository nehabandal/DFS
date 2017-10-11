package edu.usfca.cs.dfs.controller;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by npbandal on 10/9/17.
 */

public class ControllerHeartBeat {
    String hostName;

    public static void main(String[] args) throws IOException {

        ServerSocket srvSocket = new ServerSocket(8080);
        System.out.println("Controller is ready to receive heartbeat");
        Heartbeat heartbeat = new Heartbeat();
        heartbeat.receive(srvSocket);

    }
}

// collect list of hostnames from gethostname and compare your by checkin  wheather it's in gethostname list
// if contains in list then send alive mesg else send dead node
