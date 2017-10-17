package edu.usfca.cs.dfs.client;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by npbandal on 10/7/17.
 */
public class ClientReadFile {

    public void read(String controllerHost, String fileName) throws InterruptedException {

        LinkedHashMap<Integer, String> HostID = new LinkedHashMap<>();
        Map<String, String> hostFilesNames = new LinkedHashMap<>();
        byte[] chunkbytes = null;

        ClientProtoBuf cp = new ClientProtoBuf();
        HostID.put(9992, "ML-ITS-601927");
        HostID.put(9993, "ML-ITS-601927");
        HostID.put(9994, "ML-ITS-601927");
        int i = 1;

        hostFilesNames = cp.clientToControllerread(controllerHost, 9900, fileName, 1, 1, "read");

        Iterator it = hostFilesNames.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry fileHostname = (Map.Entry) it.next();
            String hostname = String.valueOf(fileHostname.getValue());
            String chunkname = String.valueOf(fileHostname.getKey());
            System.out.println("Filename: " + fileHostname.getKey() + " hostname: " + fileHostname.getValue());
            chunkbytes = cp.sendReadReqToStorageNode(hostname, 9901, chunkname);
            System.out.println(new String(chunkbytes));
            Thread.sleep(100);
        }
    }

}


