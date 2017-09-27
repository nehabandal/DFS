package edu.usfca.cs.dfs;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class StorageNode {
    public static void main(String[] args)
            throws Exception {
        String hostname = getHostname();
        System.out.println("Starting storage node on " + hostname + "...");
        new StorageNode().start();
    }

    public void start()
            throws Exception {

        System.out.println("Listening storage node on port 9995...");

        ServerSocket srvSocket = new ServerSocket(9995);
        Socket socket = srvSocket.accept();
        StorageMessages.StorageMessageWrapper msgWrapper
                = StorageMessages.StorageMessageWrapper.parseDelimitedFrom(
                socket.getInputStream());

        if (msgWrapper.hasStoreChunkMsg()) {
            StorageMessages.StoreChunk storeChunkMsg
                    = msgWrapper.getStoreChunkMsg();
            System.out.println("Storing file name: "
                    + storeChunkMsg.getFileName());
        }

        socket.close();
        srvSocket.close();
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
