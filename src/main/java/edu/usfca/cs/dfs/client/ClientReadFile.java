package edu.usfca.cs.dfs.client;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by npbandal on 10/7/17.
 */
public class ClientReadFile {

    public void read(String fileName) throws InterruptedException {

        LinkedHashMap<Integer, String> HostID = new LinkedHashMap<>();
//        cp.protoBufToSendReq(9993, fileName);
        ClientProtoBuf cp = new ClientProtoBuf();
//        HostID = controller.gethostname(); need to get updated with host and chunk details
        HostID.put(9992, "ML-ITS-601927");
        HostID.put(9993, "ML-ITS-601927");
        HostID.put(9994, "ML-ITS-601927");
        int i = 1;
        for (Map.Entry<Integer, String> entry : HostID.entrySet()) {
            cp.protoBufToSendReadmetadataToStorageNode("localhost", 9001, fileName, i);
            i++;
            Thread.sleep(100);
        }


    }

}
