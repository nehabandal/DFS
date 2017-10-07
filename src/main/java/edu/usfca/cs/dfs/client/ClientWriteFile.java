package edu.usfca.cs.dfs.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

/**
 * Created by npbandal on 10/7/17.
 */
public class ClientWriteFile {
    private File fileName;

    public ClientWriteFile(File filename) {
        this.fileName = filename;
    }

    ClientProtoBuf cp = new ClientProtoBuf();

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

    public void write(File fileName) {
        List<byte[]> fileInChunks;
        fileInChunks = splitFile(fileName);
        HashMap<String,Integer> hostPort= new HashMap<String,Integer>();
        for (int j = 0; j < fileInChunks.size(); j++) {
            String chunkname = fileName.getName() + "_chunk_" + j;
            // Why does the controller need chunk name = Adding temporary
            // List<String> hosts = controllerProtobuf.getHostNames();
            // StorageProtobuf sp = new StorageProtoBuf(host);
            // sp.write(chunk, host);
//            cp.protoBufToSendReq(9991, chunkname);
//            hostinfo = pb.protoBufToReceiveResponseFromControllerAtClientSide(9999);
            cp.protoBufToWriteintoStorageNode("ML-ITS-601927", 9992, fileInChunks.get(j));
        }

    }

}
