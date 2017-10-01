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
        for (File fileNameChunk : fileInChunks) {
            client.writeRequestToController(fileNameChunk);
        }
        pb.protoBufToReceiveResponseFromController(9999);

    }

    @Override
    public void readRequestToController() {

    }

    @Override
    public void readFromStorageNode() {

    }

    @Override
    public void writeRequestToController(File chunk) {
        protoBufToSendReqToController(9998, chunk.getName());

    }

    @Override
    public void writeIntoStorageNode() {

    }
}

