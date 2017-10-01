package edu.usfca.cs.dfs;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class Controller extends ProtoBuf {

    public static void main(String[] args) throws IOException, InterruptedException {
        List<String> hostNames = Arrays.asList("Bass1", "Bass2", "Bass3");
        ProtoBuf pb = new ProtoBuf();
        System.out.println("Controller listening on port 9998...");
        pb.protoBufToReceiveRequestFromCLient(9998, "Request received from client ");
        pb.protoBufToSendResponseToClient(9999, hostNames);
    }
}
