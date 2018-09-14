/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
//package skeletonbroadcast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

/**
 *
 * @author fno
 */
public class Client {

    protected static ClientListener listener;
    protected static BufferedReader in;
    protected static PrintWriter out;

    public static void main(String[] args) {
        Socket socket = null;

        Scanner sc = new Scanner(System.in);

        try {
            String host = "localhost";
            int port = 8010;
            if (args.length == 2) {
                host = args[0];
                port = Integer.parseInt(args[1]);
            } else if (args.length == 1) {
                port = Integer.parseInt(args[0]);
            } else if (args.length > 2) {
                throw new IllegalArgumentException();
            }
            InetAddress addr = InetAddress.getByName(host);
            socket = new Socket(addr, port);

            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
            if (socket.isConnected()) {// if the conncetion is successful then we start a listener thread

                listener = new ClientListener(in);
                listener.start();
                System.out.println("Listener activated...");
            }

            String fromClient = "";
            while (listener.isAlive() && (fromClient = sc.nextLine()) != null) {
                if (listener.isAlive()) { // check if the listener thread is alive
                    out.println(fromClient);
                    out.flush();
                }
            }
            
        } catch (IllegalArgumentException ex) {
            System.out.println("USAGE: java Client");
            System.out.println("USAGE: java Client 'port'");
            System.out.println("USAGE: java Client 'host' 'port'");
        } catch (NullPointerException ex) {

        } catch (IOException ex) {
            System.out.println("It looks likes the server unexpected crash");
        } finally {
            try {
                if (socket != null) {
                    System.out.println("Connection with client closed");
                    socket.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (IOException ex) {

            }
        }
        System.out.println("Exiting");
    }
}
