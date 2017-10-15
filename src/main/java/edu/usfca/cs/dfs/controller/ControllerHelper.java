package edu.usfca.cs.dfs.controller;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

/**
 * Created by npbandal on 10/9/17.
 */
public class ControllerHelper {

    public void receiveClientReqAtController(ServerSocket srvSocket, String msg, List<String> sublist) throws IOException {
        int chunknum = 0;
        int chunkID = 0;
        System.out.println("hey");
        srvSocket = new ServerSocket(9900);
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
        System.out.println("Size of activeNodes from controller to write file " + sublist.size());
        ControllerProtobuf.ListOfHostnames msgWrapperRes =
                ControllerProtobuf.ListOfHostnames.newBuilder()
                        .addAllHostnames(sublist)
                        .build();
        msgWrapperRes.writeDelimitedTo(clientSocket.getOutputStream());
        clientSocket.close();
    }
}
