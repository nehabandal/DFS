package edu.usfca.cs.dfs;

import java.io.File;

/**
 * Created by npbandal on 10/1/17.
 */
public interface ClientWrite {

    void writeRequestToController(String chunkName);

    void writeIntoStorageNode(String hostname, byte[] chunk);

}
