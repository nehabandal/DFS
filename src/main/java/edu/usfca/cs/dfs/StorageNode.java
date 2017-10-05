package edu.usfca.cs.dfs;

import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class StorageNode extends ProtoBuf {

    public static void main(String[] args)
            throws Exception {
        String hostname = getHostname();

        ProtoBuf pb = new ProtoBuf();
        System.out.println("Starting storage node on " + hostname + "...");
        pb.protoBufToReceiveRequestFromClientAtStorageNode(9990, "File received ");

        while (true) {
            pb.protoBufToSendHeartbeatFromStorageNodeToController(9010, "Bass01");
            pb.protoBufToSendHeartbeatFromStorageNodeToController(9010, "Bass02");
            pb.protoBufToSendHeartbeatFromStorageNodeToController(9010, "Bass03");
        }
//            pb.protoBufToSendHeartbeatFromStorageNodeToController(sockController, "Bass02");
//            pb.protoBufToSendHeartbeatFromStorageNodeToController(sockController, "Bass03");
    }

    /**
     * Retrieves the short host name of the current host.
     *
     * @return name of the current host
     */
    private static String getHostname()
            throws UnknownHostException {
        return InetAddress.getLocalHost().getHostName();
    }


}
