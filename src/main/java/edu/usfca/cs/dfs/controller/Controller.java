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
        final List<String>[] sublist = new List[]{new ArrayList<>()};
        Thread thread1 = new Thread() {
            public void run() {
                ServerSocket srvSocket = null;
                HashMap<String, Integer> activeHostnames = new HashMap<>();
                try {
                    srvSocket = new ServerSocket(8080);
                    int j = 0;
                    while (!srvSocket.isClosed()) {
                        Heartbeat heartbeat = new Heartbeat();
                        String hostname = heartbeat.receive(srvSocket);
                        hosts.add(hostname);
                        if (hosts.size() == 3) {
                            while (j < 3) {
                                sublist[0].add(hosts.get(j));
                                j++;
                            }
                        }
                        System.out.println("Sublist size: " + sublist[0].size());
                    }


                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        };

        System.out.println("Controller receiving Client request on port 9900...");

        final Thread thread2 = new Thread() {
            public void run() {
                ControllerHelper cp = new ControllerHelper();
                ServerSocket srvSocket = null;
                try {
                    srvSocket = new ServerSocket(9900);
                    cp.receiveClientReqAtController(srvSocket, "File received ", sublist[0]);

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




