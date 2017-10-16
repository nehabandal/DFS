package edu.usfca.cs.dfs.controller;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

import static edu.usfca.cs.dfs.controller.Controller.TIMEOUT_MS;

/**
 * Created by npbandal on 10/9/17.
 */
public class ControllerHelper {

    public void receiveClientReqAtController(ServerSocket srvSocket, String msg, Map<String, Controller.OnlineStorageNode> heartBeatNodes) throws IOException {
        int chunkID = 0;
        String reqType = null;
        String filenameClient = null;

        while (true) {
            Socket clientSocket = srvSocket.accept();
            ControllerProtobuf.ControllerMessagePB msgWrapper = ControllerProtobuf.ControllerMessagePB
                    .parseDelimitedFrom(clientSocket.getInputStream());
            if (msgWrapper.hasClienttalk()) {
                ControllerProtobuf.ClientTalk clientReq = msgWrapper.getClienttalk();
                chunkID = clientReq.getChunkId();
                reqType = clientReq.getReqtype();
                System.out.println("Request type is " + reqType);
                filenameClient = clientReq.getChunkName();
                System.out.println(msg + clientReq.getChunkName());
            }

            //Sending response to controller
            if (Objects.equals(reqType, "write")) {
                List<String> sublist = getAliveHostsWrite(heartBeatNodes);
                System.out.println("Coming from controller: " + heartBeatNodes.size());
                System.out.println("Size of activeNodes from controller to write file " + sublist.size());
                ControllerProtobuf.ListOfHostnames msgWrapperReswrite =
                        ControllerProtobuf.ListOfHostnames.newBuilder()
                                .addAllHostnames(sublist)
                                .build();
                msgWrapperReswrite.writeDelimitedTo(clientSocket.getOutputStream());
            }

            if (Objects.equals(reqType, "read")) {
                HashMap<String, String> sublisthosts = getAliveHostsRead(heartBeatNodes, filenameClient);
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

    private HashMap<String, String> getAliveHostsRead(Map<String, Controller.OnlineStorageNode> heartBeatNodes, String filenameClient) {
        HashMap<String, String> hostFilesNames = new LinkedHashMap<>();
        System.out.println("Coming from controller read: " + heartBeatNodes.size());
        Random random = new Random();
        int i = 0;
        List<String> keys = new ArrayList<String>(heartBeatNodes.keySet());
        while (i < heartBeatNodes.size()) {
            String randomKey = keys.get(random.nextInt(keys.size()));
            System.out.println(randomKey);
            Controller.OnlineStorageNode node = heartBeatNodes.get(randomKey);
            for (String filename : node.filenames) {
                System.out.println(filename);
                if (filename.startsWith(filenameClient)) {
                    hostFilesNames.put(filename, randomKey);
                }
            }
            i++;
        }
        return hostFilesNames;
    }


    private List<String> getAliveHostsWrite(Map<String, Controller.OnlineStorageNode> heartbeatMap) {
        final List<String> hosts = new ArrayList<>();
        Random random = new Random();
        List<String> keys = new ArrayList<String>(heartbeatMap.keySet());
        int i = 0;
        while (i < heartbeatMap.size()) {
            String randomKey = keys.get(random.nextInt(keys.size()));
            Controller.OnlineStorageNode node = heartbeatMap.get(randomKey);
            if (node.lastSeenTime - System.currentTimeMillis() < TIMEOUT_MS) {
                if (node.availableSpace > 10) {
                    if (hosts.contains(randomKey)) {
                        continue;
                    } else
                        hosts.add(randomKey);
                    System.out.println("Heartbeat: " + randomKey);
                }
            }
            if (hosts.size() == 3) {
                break;
            }
            i++;
        }
        return hosts;

    }
}
