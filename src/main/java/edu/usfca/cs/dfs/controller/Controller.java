package edu.usfca.cs.dfs.controller;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Controller {

    public static final int TIMEOUT_MS = 10000;

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
        List<String> filenames;


        public OnlineStorageNode(String host) {
            this.host = host;
        }
    }

    private Map<String, Map<Long, List<String>>> hostNameSpaceFiles = new HashMap<>();

    private final Map<String, OnlineStorageNode> heartbeatMap = new LinkedHashMap<>();

    private Thread createHeartBeatReceiverThread() {
        return new Thread() {
            public void run() {
                ServerSocket srvSocket = null;
                try {
                    srvSocket = new ServerSocket(8080);
                    while (!srvSocket.isClosed()) {
                        Heartbeat heartbeat = new Heartbeat();
                        hostNameSpaceFiles = heartbeat.receive(srvSocket);
                        getHostNameSpaceFiles(hostNameSpaceFiles);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        };
    }

    protected void getHostNameSpaceFiles(Map<String, Map<Long, List<String>>> hostNameSpaceFiles) {

        for (Map.Entry<String, Map<Long, List<String>>> parentPair : hostNameSpaceFiles.entrySet()) {
            String hostname = parentPair.getKey();
            for (Map.Entry<Long, List<String>> childPair : (parentPair.getValue()).entrySet()) {
                Long availableSpace = childPair.getKey();
                List<String> files = childPair.getValue();
                OnlineStorageNode node = heartbeatMap.get(hostname);
                if (node == null) {
                    node = new OnlineStorageNode(hostname);
                    node.lastSeenTime = System.currentTimeMillis();
                    node.availableSpace = availableSpace;
                    node.filenames = files;
                    heartbeatMap.put(hostname, node);
                } else {
                    if (System.currentTimeMillis() - node.lastSeenTime > TIMEOUT_MS) {
                        heartbeatMap.remove(hostname);
                    }
                }
            }
        }
    }

    private Thread createClientResponseThread() {
        return new Thread() {
            public void run() {
                ControllerHelper cp = new ControllerHelper();
                try {
                    ServerSocket srvSocket = new ServerSocket(9900);
//                    getHostNameSpaceFiles(hostNameSpaceFiles);
                    cp.receiveClientReqAtController(srvSocket, "File received ", heartbeatMap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
    }
}




