package edu.usfca.cs.dfs.controller;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

    public static class FilesNode {
        final String host;
        List<String> filenames;

        public FilesNode(String host) {
            this.host = host;
        }
    }

    private final Map<String, OnlineStorageNode> heartbeatMap = new LinkedHashMap<>();
    private final Map<String, FilesNode> hostNameFiles = new LinkedHashMap<>();


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
                        HashMap<String, List<String>> filesinhost = heartbeat.receiveFilenames(srvSocket);

                        for (Map.Entry<String, List<String>> entry : filesinhost.entrySet()) {
                            String hostname = entry.getKey();
                            System.out.println(hostname);
                            List<String> files = entry.getValue();
                            for (String file : files) {
                                System.out.println(file);
                            }
                            FilesNode node = hostNameFiles.get(hostname);
                            if (node == null) {
                                node = new FilesNode(hostname);
                                hostNameFiles.put(hostname, node);
                            }
                            node.filenames = files;
                        }

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

    private Thread createClientResponseThread() {
        return new Thread() {
            public void run() {
                ControllerHelper cp = new ControllerHelper();
                try {
                    ServerSocket srvSocket = new ServerSocket(9900);
                    cp.receiveClientReqAtController(srvSocket, "File received ", heartbeatMap, hostNameFiles);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
    }
}




