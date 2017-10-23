package edu.usfca.cs.dfs.controller;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

/**
 * Created by npbandal on 10/9/17.
 */
public class ControllerHelper {

    public synchronized void receiveClientReqAtController(ServerSocket srvSocket, String msg, Map<String, Controller.OnlineStorageNode> heartBeatNodes) throws IOException {
        String reqType = null;
        String filenameClient = null;

        while (true) {
            Socket clientSocket = srvSocket.accept();
            ControllerProtobuf.ControllerMessagePB msgWrapper = ControllerProtobuf.ControllerMessagePB
                    .parseDelimitedFrom(clientSocket.getInputStream());
            if (msgWrapper.hasClienttalk()) {
                ControllerProtobuf.ClientTalk clientReq = msgWrapper.getClienttalk();
                reqType = clientReq.getReqtype();
                System.out.println("Request type is " + reqType);
                filenameClient = clientReq.getChunkName();
                System.out.println(msg + clientReq.getChunkName());
            }

            //Sending response to controller
            if (Objects.equals(reqType, "write")) {
                Map<String, Long> sublist = getAliveHostsWrite(heartBeatNodes);
                System.out.println("Coming from controller: " + heartBeatNodes.size());
                System.out.println("Size of activeNodes from controller to write file " + sublist.size());
                ControllerProtobuf.ListOfHostnames msgWrapperReswrite =
                        ControllerProtobuf.ListOfHostnames.newBuilder()
                                .putAllHostnames(sublist)
                                .build();
                msgWrapperReswrite.writeDelimitedTo(clientSocket.getOutputStream());
            }

            if (Objects.equals(reqType, "read")) {
                TreeMap<String, String> sublisthosts = getAliveHostsRead(heartBeatNodes, filenameClient);
                System.out.println("Coming from controller: " + heartBeatNodes.size());
                System.out.println("Size of activeNodes from controller to read " + filenameClient + " file " + sublisthosts.size());
                ControllerProtobuf.HostNamesFiles msgWrapperResread =
                        ControllerProtobuf.HostNamesFiles.newBuilder()
                                .putAllHostNameFile(sublisthosts)
                                .build();
                msgWrapperResread.writeDelimitedTo(clientSocket.getOutputStream());
            }
            clientSocket.close();
        }
    }

    private TreeMap<String, String> getAliveHostsRead(Map<String, Controller.OnlineStorageNode> heartBeatNodes, String filenameClient) {
        TreeMap<String, String> hostFilesNames = new TreeMap<>();
        System.out.println("Coming from controller read: " + heartBeatNodes.size());
        for (String hostname : heartBeatNodes.keySet()) {
            System.out.println(hostname);
            Controller.OnlineStorageNode node = heartBeatNodes.get(hostname);
            System.out.println("Files in node: " + hostname + ": " + node.filenames.size());
            for (String filename : node.filenames) {
                if (filename.startsWith(filenameClient)) {
                    hostFilesNames.put(filename, hostname);
                }
            }
        }
        return hostFilesNames;
    }

    public synchronized void receiveClientReqAtControllerFilecorrupt(ServerSocket srvSocket, String msg, Map<String, Controller.OnlineStorageNode> heartBeatNodes) throws IOException {
        String filenameClient = null;
        String hostnamecorrupted;

        while (true) {
            Socket clientSocket = srvSocket.accept();
            ControllerProtobuf.ControllerMessagePB msgWrapper = ControllerProtobuf.ControllerMessagePB
                    .parseDelimitedFrom(clientSocket.getInputStream());
            if (msgWrapper.hasClienttalk()) {
                ControllerProtobuf.ClientTalk clientReq = msgWrapper.getClienttalk();
                filenameClient = clientReq.getChunkName();
                hostnamecorrupted = clientReq.getHostName();
                System.out.println(msg + clientReq.getChunkName());

                String newHostname = getHostForFileCorrupt(filenameClient, heartBeatNodes, hostnamecorrupted);

                //Responding back to client
                System.out.println("Coming from controller for corrupted filename: " + newHostname);
                ControllerProtobuf.ClientTalk msgWrapperResread =
                        ControllerProtobuf.ClientTalk.newBuilder()
                                .setHostName(newHostname)
                                .build();
                msgWrapperResread.writeDelimitedTo(clientSocket.getOutputStream());
            }
        }
    }

    private String getHostForFileCorrupt(String filenameClient, Map<String, Controller.OnlineStorageNode> heartBeatNodes, String hostnamecorrupted) {

        for (String hostname : heartBeatNodes.keySet()) {
            System.out.println(hostname);
            if (!hostname.equals(hostnamecorrupted)) {
                Controller.OnlineStorageNode node = heartBeatNodes.get(hostname);
                System.out.println("Files in node: " + hostname + ": " + node.filenames.size());
                for (String filename : node.filenames) {
                    if (filename.equals(filenameClient)) {
                        return hostname;
                    }
                }
            }
        }
        return null;
    }

    private Map<String, Long> getAliveHostsWrite(Map<String, Controller.OnlineStorageNode> heartbeatMap) {
        Map<String, Long> hosts = new LinkedHashMap<>();

        Random random = new Random();
        List<String> keys = new ArrayList<String>(heartbeatMap.keySet());
        int i = 0;
        while (i < heartbeatMap.size()) {
            String randomKey = keys.get(random.nextInt(keys.size()));
            Controller.OnlineStorageNode node = heartbeatMap.get(randomKey);
            if (node.availableSpace > 10) {
                if (hosts.containsKey(randomKey)) {
                    continue;
                } else
                    hosts.put(randomKey, node.availableSpace);
                System.out.println("Heartbeat: " + randomKey);
            }
            if (hosts.size() == 3) {
                break;
            }
            i++;
        }
        return hosts;

    }
}
