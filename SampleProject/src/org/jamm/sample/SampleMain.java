package org.jamm.sample;

import org.jamm.server.SimpleJsonServer;

public class SampleMain {
    public static void main(String args[]){
        System.out.print("hello world!");
        SimpleJsonServer s = new SimpleJsonServer();
        s.open();
        try {
            //Thread.sleep(1000);
           // s.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
