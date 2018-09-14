/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
//package skeletonbroadcast;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author fno
 * Source : "Object-Oriented Software Development Using Java" - Xiaoping Jia
 * Material used: client-sever echo program, multithreading and broadcast. 
 * 
 * 
 */
public class Server {
    static protected Set activeClients = new HashSet();
    public static synchronized Iterator getIterableOfClients(){
        return activeClients.iterator();
    }
   
    /**
     * 
     * 
     * @param args the command line arguments
     */
    public static void main(String[] args) {
       
        int i =  1;
        try {
            InetAddress addr = InetAddress.getByName("127.0.0.1");

            ServerSocket s = new ServerSocket(8010);
            
            System.out.println("Server started... ");
            while(true){
                Socket incoming = s.accept();
                System.out.println("New Client connecting...");
                BroadcastClientHandler newClient = new BroadcastClientHandler(incoming,i++);
                activeClients.add(newClient);
                System.out.println("So far " + activeClients.size() + " connected users");
                newClient.start();
            }
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
}
