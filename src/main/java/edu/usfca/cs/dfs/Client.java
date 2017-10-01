package edu.usfca.cs.dfs;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Client extends ProtoBuf implements ClientRead, ClientWrite {

    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        List<File> fileInChunks;
        Client client = new Client();
        ProtoBuf pb = new ProtoBuf();
        fileInChunks = pb.splitFile(new File("/Users/npbandal/BigData/p1-nehabandal/TXT.rtf"));
        client.writeRequestToController(fileInChunks);
    }

    @Override
    public void readRequestToController() {

    }

    @Override
    public void readFromStorageNode() {

    }

    @Override
    public void writeRequestToController(List<File> fileInChunks) {
        for (File chunk : fileInChunks) {
            String chunkName = chunk.getName();
            protoBufToSendReq(9998, chunkName);
        }

    }

    @Override
    public void writeIntoStorageNode() {

    }
}

