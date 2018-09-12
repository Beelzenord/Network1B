/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package skeletonbroadcast;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author fno
 */
public class ClientListener extends Thread {

    private BufferedReader in;

    public ClientListener(BufferedReader incomingStream) {
        this.in = incomingStream;
    }

    @Override
    public void run() {

        try {
            while (true) {
                System.out.println("hello");
                Object toClient = in.readLine();
                if (toClient instanceof ArrayList<?>) {
                    ArrayList<String> tmp = (ArrayList<String>) toClient;
                    Iterator iter = tmp.iterator();
                    while (iter.hasNext()) {
                        System.out.println(iter.next());
                    }
                } else if (toClient instanceof String) {
                    System.out.println(((String) toClient).trim());
                } else if (toClient == null) {
                    System.out.println("BREAK OFF");
                    break;
                }

            }

        } catch (IOException ex) {
            Logger.getLogger(ClientListener.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
