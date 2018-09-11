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
    public static void main(String[] args){
         BufferedReader inFromUser
                = new BufferedReader(new InputStreamReader(System.in));
        try {
            String host;
            if(args.length > 0){
                host = args[0];
            }else{
                host = "localhost";
            }
            Socket socket = new Socket(host,8010);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
            if(socket.isConnected()){
                System.out.println("Socket is connected ");
                System.out.println("Make Client thread here");
            }
           
           String intro = "";
           int i =2;
            while(i!=0){
                intro = in.readLine();
                System.out.println(intro);
                i--;
            }
            
            String fromClient = "";
            while(true){
               // System.out.println("in main");
                fromClient = inFromUser.readLine();
               // System.out.println("from Client : " + fromClient);
                out.println(fromClient);
                out.flush();
                
                String str = in.readLine();
                if(str ==null){
                    break;
                }
                else{
                    System.out.println(str);
                }
                
              //  out.print("[Client] " + System.in);
              //  out.flush();
            }
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
