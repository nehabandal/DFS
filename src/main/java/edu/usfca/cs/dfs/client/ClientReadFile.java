package edu.usfca.cs.dfs.client;

import java.io.FileOutputStream;
import java.util.*;

/**
 * Created by npbandal on 10/7/17.
 */
public class ClientReadFile {

    public void read(String controllerHost, String fileName) throws InterruptedException {

        LinkedHashMap<Integer, String> HostID = new LinkedHashMap<>();
        Map<String, String> hostFilesNames = new TreeMap<>();
        byte[] chunkbytes = null;
        List<byte[]> allChunkData = new ArrayList<>();
        String hostnamefilecorrupt;
        ClientProtoBuf cp = new ClientProtoBuf();
        HostID.put(9992, "ML-ITS-601927");
        HostID.put(9993, "ML-ITS-601927");
        HostID.put(9994, "ML-ITS-601927");

        hostFilesNames = cp.clientToControllerread(controllerHost, 13000, fileName, 1, 1, "read");

        Iterator it = hostFilesNames.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry fileHostname = (Map.Entry) it.next();
            String hostname = String.valueOf(fileHostname.getValue());
            String chunkname = String.valueOf(fileHostname.getKey());
            System.out.println("Filename: " + fileHostname.getKey() + "Hostname :" + fileHostname.getValue());
            chunkbytes = cp.sendReadReqToStorageNode(hostname, 13001, chunkname);

            if (chunkbytes == null) {
                hostnamefilecorrupt = cp.clientToControllerreadfilecurrupt(controllerHost, 13010, chunkname, hostname);
                chunkbytes = cp.sendReadReqToStorageNode(hostnamefilecorrupt, 13001, chunkname);
            }
            System.out.println(new String(chunkbytes));
            allChunkData.add(chunkbytes);
            Thread.sleep(100);
        }


        try (FileOutputStream fop = new FileOutputStream(fileName)) {
            for (byte[] chunkdata : allChunkData) {
                System.out.println(new String(chunkdata));
                fop.write(chunkdata);
            }
            fop.flush();
            fop.close();
        } catch (Exception e) {
            System.out.println("No file written");
        }


    }
}


