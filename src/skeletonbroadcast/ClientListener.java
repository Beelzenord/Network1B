/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
//package skeletonbroadcast;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author fno this class displays text to the user asynchronously while the
 * main Client thread may write to it
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
                String toClient = in.readLine();
                System.out.println(((String) toClient).trim());
                if (toClient == null) {
                    System.out.println("BREAK OFF");
                    break;
                }

            }

        } catch (IOException ex) {
            System.out.println("Client listener IOE");
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                System.out.println("could not close incomingStream");
            }
        }

    }

}
