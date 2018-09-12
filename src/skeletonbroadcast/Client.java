/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package skeletonbroadcast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author fno
 */
public class Client {
    private static ClientListener listener;
    public static void main(String[] args){
        Socket socket =null;
         BufferedReader inFromUser
                = new BufferedReader(new InputStreamReader(System.in));
        try {
            String host;
            if(args.length > 0){
                host = args[0];
            }else{
                host = "localhost";
            }
            socket = new Socket(host,8010);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
            if(socket.isConnected()){ // if the conncetion is successful then we start a listener thread
                                  
                listener = new ClientListener(in);
                listener.start();
                System.out.println("Listener activated....");
                System.out.println("initiate");
            }
         
            
            String fromClient = "";
            while(listener.isAlive()){
               
                fromClient = inFromUser.readLine();
               // Sy stem.out.println("from Client : " + fromClient);
                if(listener.isAlive()){
                    out.println(fromClient);
                    out.flush();
                }

            }
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally{
            try {
                socket.close();
            } catch (IOException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
