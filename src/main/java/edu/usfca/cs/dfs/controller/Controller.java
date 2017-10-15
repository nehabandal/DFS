package edu.usfca.cs.dfs.controller;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class Controller {

    public static void main(String[] args) throws IOException, InterruptedException {
        LinkedHashMap<String, List<String>> hostChunkNames = new LinkedHashMap<>();

        System.out.println("Controller receiving heartbeats on port 8080...");
        final List<String> hosts = new ArrayList<>();
        Thread thread1 = new Thread() {
            public void run() {
                ServerSocket srvSocket = null;
                HashMap<String, Integer> activeHostnames = new HashMap<>();
                try {
                    srvSocket = new ServerSocket(8080);
                    int i = 0;
                    while (!srvSocket.isClosed()) {
                        Heartbeat heartbeat = new Heartbeat();
                        String hostname = heartbeat.receive(srvSocket);
                        hosts.add(hostname);
                        System.out.println(hosts.size());
                        System.out.println(hosts.get(i));
                        i++;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        };

        System.out.println("Controller receiving Client request on port 9900...");

        Thread thread2 = new Thread() {
            public void run() {
                ControllerHelper cp = new ControllerHelper();
                ServerSocket srvSocket = null;
                try {
                    srvSocket = new ServerSocket(9900);
                    List<String> activeNodes = new ArrayList<>();
                    int i = 0;
                            cp.receiveClientReqAtController(srvSocket, "File Received: ", hosts);
                    System.out.println("Waiting for storage nodes");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };

        thread1.start();
        thread2.start();

        thread1.join();
        thread2.join();


    }
}




