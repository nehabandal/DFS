package edu.usfca.cs.dfs.storage;

import java.net.Socket;

/**
 * Created by npbandal on 10/8/17.
 */
public class WriteThread extends Thread {
    protected Socket socket;
    private StorageNodeHelper sh = new StorageNodeHelper();
    private volatile boolean running = true;

    public WriteThread(Socket clientSocket) {
        this.socket = clientSocket;
    }

    public void run() {
            try {
                byte[] chunkdata = null;
                StorageProtobuf.StorageMessagePB recfilechunks = StorageProtobuf.StorageMessagePB.parseDelimitedFrom(socket.getInputStream());
                String reqWrite = recfilechunks.getStoreChunkMsgOrBuilder().getReqtypewrite();
                String reqRead = recfilechunks.getRetrieveChunkFileMsgOrBuilder().getReqtyperead();
                if (reqWrite.equals("write")) {
                    sh.processClientWriteRequest(recfilechunks);
                }
                if (reqRead.equals("read")) {
                    System.out.println("hello");
                    chunkdata = sh.recClientChunkDataRequest(recfilechunks);
                    sh.sendChunkDatatoClient(9994, chunkdata);
                }
            } catch (Exception e) {
                e.printStackTrace();

        }
    }
}
