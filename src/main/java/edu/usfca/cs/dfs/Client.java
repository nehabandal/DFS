package edu.usfca.cs.dfs;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Client extends ProtoBuf implements ClientRead, ClientWrite {

    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        File file = new File("/Users/npbandal/BigData/p1-nehabandal/expectedOutput8000");
        List<byte[]> fileInChunks;
        Client client = new Client();
        ProtoBuf pb = new ProtoBuf();
        List<String> hostnames;
        fileInChunks = pb.splitFile(file);

        for (int j = 0; j < fileInChunks.size(); j++) {
            String chunkname = file.getName() + "_chunk_" + j;
            client.writeRequestToController(chunkname);
        }

        hostnames = pb.protoBufToReceiveResponseFromControllerAtClientSide(9999);
        for (String hostname : hostnames)
            System.out.println(hostname);
        client.writeIntoStorageNode("ML-ITS-601927", fileInChunks.get(2));
    }

    @Override
    public void readRequestToController() {

    }

    @Override
    public void readFromStorageNode() {

    }

    @Override
    public void writeRequestToController(String chunkname) {
        protoBufToSendReqToControllerFromClient(9998, chunkname);

    }

    @Override
    public void writeIntoStorageNode(String hostname, byte[] chunk) {
        protoBufToSendWriteReqToStorageNodeFromClient(hostname, 9990, chunk);

    }

}

