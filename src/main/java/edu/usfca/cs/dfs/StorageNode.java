package edu.usfca.cs.dfs;

import java.io.OutputStream;
import java.io.PrintWriter;
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
            try {
//                pb.protoBufToSendReqToControllerFromClient(9000,"Hi I am here");
                Socket soc = new Socket("localhost", 9000);
                OutputStream os = soc.getOutputStream();
                PrintWriter pw = new PrintWriter(os, true);
                pw.println("Hi I am here");
            } catch (Exception e) {
                System.out.println("Error : " + e.getMessage());
            }
        }
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
