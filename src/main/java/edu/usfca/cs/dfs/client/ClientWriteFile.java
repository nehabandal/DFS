package edu.usfca.cs.dfs.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
            Map<String, Long> hostnames;
            hostnames = cp.clientToControllerwrite(controllerHost, 13000, chunkname, fileInChunks.size(), (j + 1), "write");
            System.out.println("Host from controller: " + hostnames.size());
            int chunkid = j + 1;

            Map.Entry<String, Long> entry = hostnames.entrySet().iterator().next();
            String hostname = entry.getKey();
            Long size = entry.getValue();
            hostnames.remove(hostname);

            List<String> hostToreplica = new ArrayList<>();
            for (Map.Entry<String, Long> replicas : hostnames.entrySet()) {
                String hostnameReplica = replicas.getKey();
                Long sizeReplica = replicas.getValue();
                hostToreplica.add(hostnameReplica);
                System.out.println("Writing into node: " + hostnameReplica + " Available free space in " + hostnameReplica + " is: " + sizeReplica);
            }
            System.out.println("Writing into node: " + hostname + " Available free space in " + hostname + " is: " + size);
            cp.protoBufToWriteintoStorageNode(hostname, 13001, fileName.getName(), chunkid, fileInChunks.get(j), hostToreplica);
            Thread.sleep(100);
        }

    }
}


