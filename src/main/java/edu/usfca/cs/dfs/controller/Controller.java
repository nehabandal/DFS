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

        Thread thread1 = new Thread() {
            public void run() {
                ServerSocket srvSocket = null;
                HashMap<String, Integer> activeHostnames = new HashMap<>();
                try {
                    srvSocket = new ServerSocket(8080);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Heartbeat heartbeat = new Heartbeat();
                activeHostnames = heartbeat.receive(srvSocket);
                System.out.println(activeHostnames.size());
            }
        };

        System.out.println("Controller receiving Client request on port 9900...");

        Thread thread2 = new Thread() {
            public void run() {
                ControllerHelper cp = new ControllerHelper();
                ServerSocket srvSocket = null;
                try {
                    srvSocket = new ServerSocket(9900);
                    HashMap<String, Integer> activeHostnames = new HashMap<>();
                    cp.receiveClientReqAtController(srvSocket, "File Received: ", activeHostnames);
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




