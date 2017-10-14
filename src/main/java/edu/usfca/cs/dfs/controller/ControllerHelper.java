package edu.usfca.cs.dfs.controller;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;

/**
 * Created by npbandal on 10/9/17.
 */
public class ControllerHelper {

    public void receiveClientReqAtController(ServerSocket srvSocket, String msg, HashMap<String, Integer> activeHostnames) throws IOException {
        int chunknum = 0;
        int chunkID = 0;
        while (true) {

            Socket clientSocket = srvSocket.accept();
            ControllerProtobuf.ControllerMessagePB msgWrapper = ControllerProtobuf.ControllerMessagePB
                    .parseDelimitedFrom(clientSocket.getInputStream());
            if (msgWrapper.hasClienttalk()) {
                ControllerProtobuf.ClientTalk clientReq = msgWrapper.getClienttalk();
                chunknum = clientReq.getNumChunks();
                chunkID = clientReq.getChunkId();
                System.out.println(msg + clientReq.getChunkName());
            }

            System.out.println("Size of activeNodes heartbeats: "+ activeHostnames.size());
            //Sending response to controller
            ControllerProtobuf.ListOfHostnames msgWrapperRes =
                    ControllerProtobuf.ListOfHostnames.newBuilder()
//                            .addAllHostnames(activeHostnames)
                            .build();
            msgWrapperRes.writeDelimitedTo(clientSocket.getOutputStream());

            if(chunknum == chunkID)
            {
                break;
            }
        }
        srvSocket.close();
    }
}
