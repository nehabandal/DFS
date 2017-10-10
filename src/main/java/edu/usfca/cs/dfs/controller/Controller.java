package edu.usfca.cs.dfs.controller;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

public class Controller {

    public static void main(String[] args) throws IOException, InterruptedException {
        LinkedHashMap<String, List<String>> hostChunkNames = new LinkedHashMap<>();
        List<String> activeHostnames = Arrays.asList("Bass1", "Bass2", "Bass3");

        System.out.println("Controller listening on port 9998...");
        ControllerHelper cp = new ControllerHelper();
        ServerSocket srvSocket = new ServerSocket(9900);
        cp.receiveClientReqAtController(srvSocket, "File Received: ", activeHostnames);
    }
}




