package edu.usfca.cs.dfs.client;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Client {

    final static String FILE_NAME = "expectedOutput8000";

    /**
     * client get identifier
     * client put /path/to/file # outputs identifier to stdout
     *
     * @param args
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws InterruptedException
     */
    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {

        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("read")) {
                String fileToRead = args[i + 1];
                ClientReadFile readFile = new ClientReadFile(fileToRead);
                System.out.println(args[i + 1]);
            }
            if (args[i].equals("write")) {
                String fileToWrite = args[i+1];
                File file = new File(fileToWrite);
                ClientWriteFile writeFile = new ClientWriteFile(file);

                System.out.println(args[i + 1]);
            }
        }

    }

}

