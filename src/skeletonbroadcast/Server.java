/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
//package skeletonbroadcast;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author fno
 * Source : "Object-Oriented Software Development Using Java" - Xiaoping Jia
 * 
 */
public class Server {
    static protected Set activeClients = new HashSet();
    
   
    /**
     * 
     * 
     * @param args the command line arguments
     */
    public static void main(String[] args) {
       
        int i =  1;
        try {
            ServerSocket s = new ServerSocket(8010);
            System.out.println("Server started... ");
            while(true){
                Socket incoming = s.accept();
                System.out.println("New Client connecting...");
                BroadcastClientHandler newClient = new BroadcastClientHandler(incoming,i++);
                activeClients.add(newClient);
                System.out.println("So far " + activeClients.size());
                newClient.start();
            }
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
}
