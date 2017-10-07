package edu.usfca.cs.dfs.client;

import java.io.File;
import java.util.List;

/**
 * Created by npbandal on 10/1/17.
 */
public interface ChunkHelper {

    List<byte[]> splitFile(File file);

}
