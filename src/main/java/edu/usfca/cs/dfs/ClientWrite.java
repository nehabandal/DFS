package edu.usfca.cs.dfs;

import java.io.File;
import java.util.List;

/**
 * Created by npbandal on 10/1/17.
 */
public interface ClientWrite {

    void writeRequestToController(List<File> fileInChunks);
    void writeIntoStorageNode();

}
