package edu.usfca.cs.dfs.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by npbandal on 10/7/17.
 */
public class ClientWriteFile {
    private File fileName;

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

    public void write(File fileName) throws InterruptedException {
        List<byte[]> fileInChunks;
        ClientProtoBuf cp = new ClientProtoBuf();
        fileInChunks = splitFile(fileName);
        HashMap<Integer, String> hostPort = new HashMap<Integer, String>();
        hostPort.put(9992, "ML-ITS-601927");
        hostPort.put(9993, "ML-ITS-601927");
        hostPort.put(9994, "ML-ITS-601927");

        List<String> hostnames = new ArrayList<>();

        for (int j = 0; j < fileInChunks.size(); j++) {
            String chunkname = fileName.getName() + (j + 1);
            hostnames = cp.clientToController(9000, chunkname);
            System.out.println(hostnames.size());
            cp.protoBufToWriteintoStorageNode("ML-ITS-601927", 9001, fileName.getName(),
                    j + 1, fileInChunks.get(j));
            Thread.sleep(100);
        }

    }
}


