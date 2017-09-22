package edu.usfca.cs.dfs;

import java.io.File;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class StorageNode {

    private ServerSocket srvSocket;

    public static void main(String[] args)
            throws Exception {
        String hostname = getHostname();
        long diskSize = new File("/").getTotalSpace();
        System.out.println("Total disk size:" + diskSize + " bytes");
        System.out.println("Starting storage node on " + hostname + "...");
        new StorageNode().start();
    }

    public float start()
            throws Exception {
        srvSocket = new ServerSocket(9999);
        float total = 0, total1;
        System.out.println("Listening...");
        while (true) {
            Socket socket = srvSocket.accept();
            StorageMessages.StorageMessageWrapper msgWrapper
                    = StorageMessages.StorageMessageWrapper.parseDelimitedFrom(
                    socket.getInputStream());

            if (msgWrapper.hasStoreChunkMsg()) {
                StorageMessages.StoreChunk storeChunkMsg
                        = msgWrapper.getStoreChunkMsg();
                System.out.println("Storing file name: "
                        + storeChunkMsg.getFileName());
                total = total + storeChunkMsg.getSerializedSize();
            }
            total1 = total / (1024 * 1024);
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
