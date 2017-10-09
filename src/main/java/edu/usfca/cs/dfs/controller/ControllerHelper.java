package edu.usfca.cs.dfs.controller;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

/**
 * Created by npbandal on 10/9/17.
 */
public class ControllerHelper {

    public void receiveClientReqAtController(ServerSocket srvSocket, String msg, List<String> activeHostnames) throws IOException {
        try {

            Socket clientSocket = srvSocket.accept();
            ControllerProtobuf.ControllerMessagePB msgWrapper = ControllerProtobuf.ControllerMessagePB
                    .parseDelimitedFrom(clientSocket.getInputStream());
            if (msgWrapper.hasClienttalk()) {
                ControllerProtobuf.ClientTalk clientReq
                        = msgWrapper.getClienttalk();
                System.out.println(msg + clientReq.getChunkName());
            }

            //Sending response to controller
            ControllerProtobuf.ListOfHostnames msgWrapperRes =
                    ControllerProtobuf.ListOfHostnames.newBuilder()
                            .addAllHostnames(activeHostnames)
                            .build();
            msgWrapperRes.writeDelimitedTo(clientSocket.getOutputStream());
            clientSocket.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
