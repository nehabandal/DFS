package edu.usfca.cs.dfs.client;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by npbandal on 10/7/17.
 */
public class ClientReadFile {
    String fileName;

    ClientProtoBuf cp = new ClientProtoBuf();

    public void read(String fileName) {

        LinkedHashMap<String, Integer> HostID = new LinkedHashMap<>();
//        cp.protoBufToSendReq(9993, fileName);
//        HostID = controller.gethostname(); need to get updated with host and chunk details
        HostID.put("ML-ITS-601927", 1);
        for (Map.Entry<String, Integer> entry : HostID.entrySet()) {
            String hostName = entry.getKey();
            int chunkID = entry.getValue();
            cp.protoBufToSendReadDataToStorageNode(hostName, 9992, fileName, chunkID);
        }
        cp.receiveChunkdataFromStorageNode(9994);

    }

}
