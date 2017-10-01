package edu.usfca.cs.dfs;

import java.io.File;

/**
 * Created by npbandal on 10/1/17.
 */
public interface ClientWrite {

    void writeRequestToController(File fileInChunks);

    void writeIntoStorageNode();

}
