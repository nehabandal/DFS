package edu.usfca.cs.dfs.controller;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.*;

public class Controller {

    public static final int COUNT = 3;
    public static final int TIMEOUT_MS = 3000;

    public static void main(String[] args) throws IOException, InterruptedException {
        final Controller controller = new Controller();
        controller.launchThreads();

    }

    private void launchThreads() throws InterruptedException {

        System.out.println("Controller receiving heartbeats on port 8080...");
        Thread thread1 = createHeartBeatReceiverThread();

        System.out.println("Controller receiving Client request on port 9900...");

        Thread thread2 = createClientResponseThread();

        thread1.start();
        thread2.start();

        thread1.join();
        thread2.join();
    }

    public static class OnlineStorageNode {
         final String host;
         long availableSpace;
         long lastSeenTime;

        public OnlineStorageNode(String host) {
            this.host = host;
        }
    }

    private final Map<String, OnlineStorageNode> heartbeatMap = new LinkedHashMap<>();

    private Thread createHeartBeatReceiverThread() {
        return new Thread() {
            public void run() {
                ServerSocket srvSocket = null;
                HashMap<String, Integer> activeHostnames = new HashMap<>();
                try {
                    srvSocket = new ServerSocket(8080);
                    while (!srvSocket.isClosed()) {
                        Heartbeat heartbeat = new Heartbeat();
                        HashMap<String, Long> hostdetails = heartbeat.receive(srvSocket);
                        for (Map.Entry<String, Long> entry : hostdetails.entrySet()) {
                            String hostname = entry.getKey();
                            Long availableSpace = entry.getValue();
                            OnlineStorageNode node = heartbeatMap.get(hostname);
                            if (node == null) {
                                node = new OnlineStorageNode(hostname);
                                heartbeatMap.put(hostname, node);
                            }
                            node.lastSeenTime = System.currentTimeMillis();
                            node.availableSpace = availableSpace;
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        };
    }

//    private List<String> getAliveHosts() {
//        final List<String> hosts = new ArrayList<>();
//        for (String host : heartbeatMap.keySet()) {
////            System.out.println("Heartbeat: " + host);
////            OnlineStorageNode node = heartbeatMap.get(host);
////            if (node.lastSeenTime - System.currentTimeMillis() < TIMEOUT_MS) {
////                if (node.availableSpace > 10) {
//                    hosts.add(host);
////                }
////            }
////            if (hosts.size() == 3) {
////                break;
////            }
//        }
//        return hosts;
//    }

    private Thread createClientResponseThread() {
        return new Thread() {
            public void run() {
                ControllerHelper cp = new ControllerHelper();
                try {
                    ServerSocket srvSocket = new ServerSocket(9900);
//                    List<String> sublist = getAliveHosts(heartbeatMap);
//                    cp.receiveClientReqAtController(srvSocket, "File received ", sublist);
                    cp.receiveClientReqAtController(srvSocket, "File received ", heartbeatMap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    private List<String> getAliveHosts(Map<String, Controller.OnlineStorageNode> heartbeatMap) {
        final List<String> hosts = new ArrayList<>();
        for (String host : heartbeatMap.keySet()) {
            System.out.println("Heartbeat: " + host);
            Controller.OnlineStorageNode node = heartbeatMap.get(host);
            if (node.lastSeenTime - System.currentTimeMillis() < TIMEOUT_MS) {
                if (node.availableSpace > 10) {
                    hosts.add(host);
                }
            }
            if (hosts.size() == 3) {
                break;
            }
        }
        return hosts;
    }
}




