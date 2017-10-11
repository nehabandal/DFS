package edu.usfca.cs.dfs.controller;

/**
 * Created by npbandal on 10/7/17.
 */
public class Heartbeat implements Runnable {
    String newhost;

    private String currenthost = null;

    public Heartbeat(String hostname) {
        newhost = hostname;
    }

    @Override
    public void run() {
        while (send(newhost))
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
    }

    public synchronized boolean send(String newHost) {

        Thread x = Thread.currentThread();

        if (x.isAlive()) {
            System.out.println("PING! (" + x.getName() + ")");
//            whoseTurn = myOpponent;
            notifyAll();
        } else {
            try {
                long t1 = System.currentTimeMillis();
                wait(3000);
                if ((System.currentTimeMillis() - t1) > 3000) {
                    System.out.println("****** TIMEOUT! " + x.getName() +
                            " is waiting for " + currenthost );
                }
            } catch (InterruptedException e) {
            }
        }
        return true;
    }
}
