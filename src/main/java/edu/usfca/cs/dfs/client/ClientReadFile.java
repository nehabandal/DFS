package edu.usfca.cs.dfs.client;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by npbandal on 10/7/17.
 */
public class ClientReadFile {
    ClientProtoBuf cp = new ClientProtoBuf();

    public void read(String fileName) {

        LinkedHashMap<Integer, String> HostID = new LinkedHashMap<>();
//        cp.protoBufToSendReq(9993, fileName);
//        HostID = controller.gethostname(); need to get updated with host and chunk details
        HostID.put(9992, "ML-ITS-601927");
        HostID.put(9993, "ML-ITS-601927");
        HostID.put(9994, "ML-ITS-601927");
        int i =1;
        for (Map.Entry<Integer, String> entry : HostID.entrySet()) {
            Integer portnum = entry.getKey();
            String hostName = entry.getValue();
            cp.protoBufToSendReadDataToStorageNode("localhost", 9995, fileName, i);
            i++;
        }
//        cp.receiveChunkdataFromStorageNode(9996);


    }

}
