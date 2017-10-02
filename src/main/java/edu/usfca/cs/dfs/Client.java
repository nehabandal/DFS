package edu.usfca.cs.dfs;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Client extends ProtoBuf implements ClientRead, ClientWrite {

    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        List<File> fileInChunks;
        Client client = new Client();
        ProtoBuf pb = new ProtoBuf();
        fileInChunks = pb.splitFile(new File("/Users/npbandal/BigData/p1-nehabandal/TXT.rtf"));
        List<String> hostnames = new ArrayList<>();
        for (File fileNameChunk : fileInChunks) {
            client.writeRequestToController(fileNameChunk.getName());
//            hostnames = pb.protoBufToReceiveResponseFromController(9999);
//            client.writeIntoStorageNode();
        }
        hostnames = pb.protoBufToReceiveResponseFromController(9999);
        for (String hostname : hostnames)
            System.out.println(hostname);
        client.writeIntoStorageNode("ML-ITS-601927",fileInChunks.get(0));
    }

    @Override
    public void readRequestToController() {

    }

    @Override
    public void readFromStorageNode() {

    }

    @Override
    public void writeRequestToController(String chunkname) {
        protoBufToSendReqToController(9998, chunkname);

    }

    @Override
    public void writeIntoStorageNode(String hostname, File chunk) {
        protoBufToSendWriteReqToStorageNode(hostname,9990,chunk);

    }

}

