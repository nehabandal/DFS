package edu.usfca.cs.dfs.controller;

import edu.usfca.cs.dfs.client.ClientProtoBuf;

import java.io.IOException;
import java.util.*;

/**
 * Created by npbandal on 10/18/17.
 */
public class BackupNode {

    public void creatBackupNode(String hostname, List<String> filenamelist, Map<String, Controller.OnlineStorageNode> heartbeatMap) throws IOException {

        TreeMap<String, byte[]> chunkbytes = new TreeMap<>();
        byte[] chunkbytesreq;
        String backupHost = null;
        ClientProtoBuf cp = new ClientProtoBuf();

        for (Map.Entry<String, Controller.OnlineStorageNode> entry : heartbeatMap.entrySet()) {
            String hostName = entry.getKey();
            Controller.OnlineStorageNode node = heartbeatMap.get(hostName);
            for (String fileDead : filenamelist) {
                if (node.filenames.contains(fileDead) && !Objects.equals(hostName, hostname)) {
                    chunkbytesreq = cp.sendReadReqToStorageNode(hostName, 13001, fileDead);
                    System.out.println("reading: " + fileDead);
                    chunkbytes.put(fileDead, chunkbytesreq);
                    System.out.println("Here I am reading dead node files: " + chunkbytes.size());

                }
            }
        }
        for (Map.Entry<String, Controller.OnlineStorageNode> entry : heartbeatMap.entrySet()) {
            String hostName = entry.getKey();
            Controller.OnlineStorageNode node = heartbeatMap.get(hostName);
            if (!node.filenames.equals(filenamelist) && !Objects.equals(hostName, hostname)) {
                System.out.println("here I am checking hostnames which doesnt contain file");
                backupHost = hostName;
            }
        }
        System.out.println("Back up host is: " + backupHost);

        Iterator it = chunkbytes.entrySet().iterator();
        while (it.hasNext()) {
            System.out.println("Here I am writing file in new node ");
            Map.Entry backup = (Map.Entry) it.next();
            String backupfilename = String.valueOf(backup.getKey());
            System.out.println("File name writing: " + backupfilename);
            byte[] chunkdata = (byte[]) backup.getValue();
            List<String> test = new ArrayList<>();
            cp.protoBufToWriteintoStorageNode(backupHost, 13001, backupfilename, 0, chunkdata, test);
        }
    }

}

