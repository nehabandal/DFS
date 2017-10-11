package edu.usfca.cs.dfs.storage;

import edu.usfca.cs.dfs.controller.ProtoHeartbeat;

import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Objects;

/**
 * Created by npbandal on 10/9/17.
 */
public class StorageNodeHeartBeat {


    public static void main(String[] args) {
        try {
            String hostname = getHostname();
            while (hostname!=null) { //should be not equal in actual code
                Socket sockController = new Socket(args[0], 8080);
                ProtoHeartbeat.StorageHearbeat heartbeat
                        = ProtoHeartbeat.StorageHearbeat.newBuilder()
                        .setHostName(hostname)
                        .build();
                ProtoHeartbeat.ControllerMessagePB msgWrapper =
                        ProtoHeartbeat.ControllerMessagePB.newBuilder()
                                .setStorageHeartBeat(heartbeat)
                                .build();

                msgWrapper.writeDelimitedTo(sockController.getOutputStream());
            }
            Thread.sleep(100);
        } catch (Exception e) {

        }
    }

    private static String getHostname() throws UnknownHostException {
        return InetAddress.getLocalHost().getHostName();
    }

}


//    public static void main(String args[]) throws Exception {
//        String hostname = getHostname();
//
//        Socket sockController = null;
//        try {
//            sockController = new Socket(hostname, 8080);
//            ProtoHeartbeat.StorageHearbeat heartbeat
//                    = ProtoHeartbeat.StorageHearbeat.newBuilder()
//                    .setHostName(hostname)
//                    .build();
//
//            ProtoHeartbeat.ControllerMessagePB msgWrapper =
//                    ProtoHeartbeat.ControllerMessagePB.newBuilder()
//                            .setStorageHeartBeat(heartbeat)
//                            .build();
//
//            msgWrapper.writeDelimitedTo(sockController.getOutputStream());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        try {
//            // Wait 5 seconds
//            Thread.currentThread().sleep(3000);
//        } catch (InterruptedException e) {
//        }
//        try {
//            Thread.currentThread().sleep(100);
//        } catch (InterruptedException e) {
//        }
//    }
//
//
//}


//        List<Thread> hostnames = new ArrayList<>();
//        hostnames.add(host);
//        hostnames.add(host2);
//        hostnames.add(host3);

//        for(Thread hostName: hostnames)

//        while (hostname != null) {
//            Thread host = new Thread(new StorageNode(hostname, heartBeat));
//            host.setName(hostname);
//            host.start();
//        }
