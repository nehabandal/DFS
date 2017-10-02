package edu.usfca.cs.dfs;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class StorageNode extends ProtoBuf {
    public static void main(String[] args)
            throws Exception {
        String hostname = getHostname();
        ProtoBuf pb = new ProtoBuf();
        System.out.println("Starting storage node on " + hostname + "...");
        pb.protoBufToReceiveRequestFromClientAtStorageNode(9990, "File received ");
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
