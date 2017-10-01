package edu.usfca.cs.dfs;

import java.io.IOException;

public class Controller extends ProtoBuf {

    public static void main(String[] args) throws IOException, InterruptedException {
        ProtoBuf pb = new ProtoBuf();
        System.out.println("Controller listening on port 9998...");
        pb.protoBufToReceiveResponse(9998, "Request received from client ");
//        pb.protoBufToSendReq(9999,"Ack from controller");
    }
}
