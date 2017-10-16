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

    public void receiveClientReqAtController(ServerSocket srvSocket, String msg, Map<String, Controller.OnlineStorageNode> heartBaetNodes) throws IOException {
        int chunknum = 0;
        int chunkID = 0;
        while (true) {
            Socket clientSocket = srvSocket.accept();
            ControllerProtobuf.ControllerMessagePB msgWrapper = ControllerProtobuf.ControllerMessagePB
                    .parseDelimitedFrom(clientSocket.getInputStream());
            if (msgWrapper.hasClienttalk()) {
                ControllerProtobuf.ClientTalk clientReq = msgWrapper.getClienttalk();
                chunkID = clientReq.getChunkId();
                chunknum = clientReq.getNumChunks();
                System.out.println(chunkID);
                System.out.println(msg + clientReq.getChunkName());
            }

            //Sending response to controller

            List<String> sublist = getAliveHosts(heartBaetNodes);
            System.out.println("Size of activeNodes from controller to write file " + sublist.size());
            ControllerProtobuf.ListOfHostnames msgWrapperRes =
                    ControllerProtobuf.ListOfHostnames.newBuilder()
                            .addAllHostnames(sublist)
                            .build();
            msgWrapperRes.writeDelimitedTo(clientSocket.getOutputStream());
            clientSocket.close();
        }
    }

    private List<String> getAliveHosts(Map<String, Controller.OnlineStorageNode> heartbeatMap) {
        final List<String> hosts = new ArrayList<>();
        Random random = new Random();
        Set<String> a = new HashSet<String>(heartbeatMap.keySet());
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
