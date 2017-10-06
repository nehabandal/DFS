package edu.usfca.cs.dfs;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Client extends ProtoBuf implements ClientRead, ClientWrite {

    final static String FILE_NAME = "expectedOutput8000";

    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        File file = new File("/Users/npbandal/BigData/p1-nehabandal/expectedOutput8000");

        List<byte[]> fileInChunks;
        Client client = new Client();
        ProtoBuf pb = new ProtoBuf();
        HashMap<String, String> hostinfo;
        fileInChunks = pb.splitFile(file);
        List<String> hostnames = new ArrayList<>();
        List<String> IPAddresses = new ArrayList<>();

        client.readRequestToController(FILE_NAME);
        hostinfo = pb.protoBufToReceiveResponseFromControllerAtClientSide(9999);
        for (Map.Entry<String, String> entry : hostinfo.entrySet()) {
            hostnames.add(entry.getKey());
            IPAddresses.add(entry.getValue());
        }
//        client.readFromStorageNode(hostnames,IPAddresses,chunk);

//        for (int j = 0; j < fileInChunks.size(); j++) {
//            String chunkname = file.getName() + "_chunk_" + j;
//            client.writeRequestToController(chunkname);
//            client.writeIntoStorageNode("ML-ITS-601927", fileInChunks.get(j));
//        }
//        hostinfo = pb.protoBufToReceiveResponseFromControllerAtClientSide(9999);

    }

    @Override
    public void readRequestToController(String filename) {
        protoBufToSendReqToControllerFromClient(9998, filename);
    }

    @Override
    public void readFromStorageNode() {
//        protoBufToSendReadReqToStorageNodeFromClient()
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

