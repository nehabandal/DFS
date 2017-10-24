package edu.usfca.cs.dfs.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by npbandal on 10/7/17.
 */
public class ClientWriteFile {

    public ClientWriteFile() {

    }

    protected List<byte[]> splitFile(File file) {
        final int CHUNK_SIZE = 1024 * 1024;
        final int nChunks = (int) Math.ceil((double) file.length() / CHUNK_SIZE);
        List<byte[]> fileInChunks = new ArrayList<>();
        try {
            final FileInputStream fis = new FileInputStream(file);
            for (int i = 0; i < nChunks; ++i) {
                final byte[] bytes = new byte[CHUNK_SIZE];
                fis.read(bytes);
                String emptyRemoved = new String(bytes).replaceAll("\u0000.*", "");
                byte[] finalarray = emptyRemoved.getBytes();
                fileInChunks.add(finalarray);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileInChunks;
    }

    public void write(String controllerHost, File fileName) throws InterruptedException, UnknownHostException {
        List<byte[]> fileInChunks;
        ClientProtoBuf cp = new ClientProtoBuf();
        fileInChunks = splitFile(fileName);

        for (int j = 0; j < fileInChunks.size(); j++) {
            String chunkname = fileName.getName() + (j + 1);
            Map<String, Long> hostnames = new TreeMap<>();
            hostnames = cp.clientToControllerwrite(controllerHost, 13000, chunkname, fileInChunks.size(), (j + 1), "write");
            System.out.println("Host from controller: " + hostnames.size());
            int chunkid = j + 1;
            List<String> hostnameslist = new ArrayList<String>(hostnames.keySet());
            List<Long> spacelist = new ArrayList<Long>(hostnames.values());
            List<String> hostToreplica = new ArrayList<>();
            hostToreplica.add(hostnameslist.get(1));
            hostToreplica.add(hostnameslist.get(2));
            System.out.println("Writing into node: " + hostnameslist.get(0) + " Available space: " + spacelist.get(0));
            System.out.println("Writing into node: " + hostnameslist.get(1) + " Available space: " + spacelist.get(1));
            System.out.println("Writing into node: " + hostnameslist.get(2) + " Available space: " + spacelist.get(2));
            cp.protoBufToWriteintoStorageNode(hostnameslist.get(0), 13001, fileName.getName(), chunkid, fileInChunks.get(j), hostToreplica);
            Thread.sleep(100);
        }
    }
}


