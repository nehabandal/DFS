package edu.usfca.cs.dfs.client;

import java.util.*;

/**
 * Created by npbandal on 10/7/17.
 */
public class ClientReadFile {

    public void read(String controllerHost, String fileName) throws InterruptedException {

        LinkedHashMap<Integer, String> HostID = new LinkedHashMap<>();
        Map<String,String> hostFilesNames = new LinkedHashMap<>();

        ClientProtoBuf cp = new ClientProtoBuf();
        HostID.put(9992, "ML-ITS-601927");
        HostID.put(9993, "ML-ITS-601927");
        HostID.put(9994, "ML-ITS-601927");
        int i = 1;

        hostFilesNames = cp.clientToControllerread(controllerHost, 9900, fileName, 1, 1,"read");

        Iterator it = hostFilesNames.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry fileHostname = (Map.Entry)it.next();
            System.out.println("Filename: "+fileHostname.getKey() + " hostname: " + fileHostname.getValue());
            it.remove(); // avoids a ConcurrentModificationException
        }
//        for (String hostname : activeHostnames) {
//            cp.sendReadReqToStorageNode("localhost", 9901, fileName, i, activeHostnames.size());
//            i++;
//            Thread.sleep(100);
//        }
    }

}


