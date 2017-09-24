package edu.usfca.cs.dfs;

import java.io.File;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

class WriteFile implements Runnable {
    private ServerSocket srvSocket;

    @Override
    public void run() {
        try {
            srvSocket = new ServerSocket(9997);
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
                }
            }
        } catch (Exception e) {

        }

    }

}

public class StorageNode {

    public static void main(String[] args)
            throws Exception {
        String hostname = getHostname();
        long diskSize = new File("/").getTotalSpace();
        System.out.println("Total disk size:" + (diskSize / (1024 * 1024)) + " bytes");
        System.out.println("Starting storage node on " + hostname + "...");
        WriteFile writeFile = new WriteFile();
        Thread t1 = new Thread(writeFile);
        t1.start();
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
