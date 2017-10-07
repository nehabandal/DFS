package edu.usfca.cs.dfs.client;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by npbandal on 10/7/17.
 */
public class ClientReadFile {
    String fileName;

    public ClientReadFile(String filename) {
        this.fileName = filename;
    }

    ClientProtoBuf cp = new ClientProtoBuf();

    public void read(String fileName) {
        HashMap<String, Map<String, Integer>> chunkhostport = new HashMap<>();
        String hostname = null;
        int portnumber = 0;
//        cp.protoBufToSendReq(9993, fileName);

//        hostport = controller.gethostname(); need to get updated with host and chunk details

        //Get data from all hostnames
        Iterator<Map.Entry<String, Map<String, Integer>>> parent = chunkhostport.entrySet().iterator();
        while (parent.hasNext()) {

            Map.Entry<String, Map<String, Integer>> parentPair = parent.next();
            System.out.println("parentPair.getKey() :   " + parentPair.getKey() + " parentPair.getValue()  :  " + parentPair.getValue());
            String chunkname = (String) parentPair.getKey();
            Iterator<Map.Entry<String, Integer>> child = (parentPair.getValue()).entrySet().iterator();
            while (child.hasNext()) {
                Map.Entry childPair = child.next();
                System.out.println("childPair.getKey() :   " + childPair.getKey() + " childPair.getValue()  :  " + childPair.getValue());
                hostname = (String) childPair.getKey();
                portnumber = (Integer) childPair.getValue();

                child.remove();
            }
            cp.protoBufToReadfromStorageNode(hostname, portnumber,chunkname);
        }
    }

}
