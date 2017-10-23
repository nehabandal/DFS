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

        Thread thread3 = deleteInactiveNodes();

        System.out.println("Controller receiving File corrupt request on port 13010...");
        Thread thread4 = createClientResponseFileCorruptThread();



        thread1.start();
        thread2.start();
        thread3.start();
        thread4.start();

        thread1.join();
        thread2.join();
        thread3.join();
        thread4.join();
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

    private static Map<String, OnlineStorageNode> heartbeatMap = new LinkedHashMap<>();

    private Thread createHeartBeatReceiverThread() {
        return new Thread() {
            public void run() {
                ServerSocket srvSocket = null;
                try {
                    srvSocket = new ServerSocket(13002);
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
                }
                node.lastSeenTime = System.currentTimeMillis();
                node.availableSpace = availableSpace;
                node.filenames = files;
                heartbeatMap.put(hostname, node);

            }
        }
    }

    private Thread createClientResponseThread() {
        return new Thread() {
            public void run() {
                ControllerHelper cp = new ControllerHelper();
                try {
                    ServerSocket srvSocket = new ServerSocket(13000);
                    cp.receiveClientReqAtController(srvSocket, "File received ", heartbeatMap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    private Thread createClientResponseFileCorruptThread() {
        return new Thread() {
            public void run() {
                ControllerHelper cp = new ControllerHelper();
                try {
                    ServerSocket srvSocket = new ServerSocket(13010);
                    cp.receiveClientReqAtControllerFilecorrupt(srvSocket, "File received ", heartbeatMap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    private Thread deleteInactiveNodes() {
        return new Thread() {
            public void run() {
                while (true) {
                    deleteInactive();
                    try {
                        sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
        };
    }

    private void deleteInactive() {
        try {
            for (Map.Entry<String, OnlineStorageNode> entry : heartbeatMap.entrySet()) {
                System.out.println(heartbeatMap.size());
                String hostname = entry.getKey();
                System.out.println(hostname);
                OnlineStorageNode node = entry.getValue();
                if (System.currentTimeMillis() - node.lastSeenTime > TIMEOUT_MS) {
                    BackupNode backup = new BackupNode();
                    System.out.println("removing host: " + hostname);
//                    backup.creatBackupNode(hostname,node.filenames,heartbeatMap);
                    heartbeatMap.remove(hostname);
                }
            }
        } catch (Exception e) {
            System.out.println("Host removed");
        }
    }
}




