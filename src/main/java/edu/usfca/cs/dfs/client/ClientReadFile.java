package edu.usfca.cs.dfs.client;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by npbandal on 10/7/17.
 */
public class ClientReadFile {

    public void read(String fileName) throws InterruptedException {

        LinkedHashMap<Integer, String> HostID = new LinkedHashMap<>();
        List<String> activeHostnames = Arrays.asList("Bass1", "Bass2", "Bass3");

        ClientProtoBuf cp = new ClientProtoBuf();
        HostID.put(9992, "ML-ITS-601927");
        HostID.put(9993, "ML-ITS-601927");
        HostID.put(9994, "ML-ITS-601927");
        int i = 1;

        activeHostnames = cp.clientToController(9000, fileName);

        System.out.println(activeHostnames.size());
        for (String hostname : activeHostnames) {
            cp.sendReadReqToStorageNode("localhost", 9001, fileName, i);
            i++;
            Thread.sleep(100);
        }
    }

}


