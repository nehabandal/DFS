package edu.usfca.cs.dfs.client;

import java.io.File;
import java.io.IOException;

public class Client {

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

        ClientWriteFile writeFile = new ClientWriteFile();
        ClientReadFile readFile = new ClientReadFile();

        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("read")) {
                String fileToRead = args[i + 1];
                readFile.read(fileToRead);
            }
            if (args[i].equals("write")) {
                String fileToWrite = args[i+1];
                File file = new File(fileToWrite);
                writeFile.write(file);
            }
        }

    }

}

