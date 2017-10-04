package edu.usfca.cs.dfs;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;

public class Controller extends ProtoBuf {


    public static void main(String[] args) throws IOException, InterruptedException {
        List<String> hostNames = Arrays.asList("Bass1", "Bass2", "Bass3");
        ProtoBuf pb = new ProtoBuf();
        System.out.println("Controller listening on port 9998...");
        pb.protoBufToReceiveRequestFromClientAtController(9998, "Request received from client ");
        pb.protoBufToSendResponseToClientFromController(9999, hostNames);
        ServerSocket s = new ServerSocket(9000);
        while (true) {
//            pb.protoBufToReceiveRequestFromClientAtController(9000,"");
            Socket c = s.accept();
            InputStream in = c.getInputStream();
            InputStreamReader inr = new InputStreamReader(in);
            BufferedReader br = new BufferedReader(inr);
            String str = br.readLine();
            System.out.println("Ping command received from : " + c.getInetAddress().getHostName() + " with string " + str);
            PrintStream ps = new PrintStream(c.getOutputStream());
            ps.println(str);
        }
    }
}
