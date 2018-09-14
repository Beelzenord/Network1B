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
    public static void main(String[] args){
        Socket socket =null;
        
         Scanner sc = new Scanner(System.in);
         
        try {
            String host;
           /* if(args.length > 0){
                
                host = args[0];
            }else{
                host = "localhost";
            }*/
            if(args.length < 2){
                System.out.println("please insert two arguments corresponding with the address and the port respectively");
            }
            if(args[0].equals("localhost")){
                socket = new Socket(args[0],8010);
            }
            else{
                 InetAddress addr = InetAddress.getByName(args[0]);
                 socket = new Socket(addr,8010);
            }
           
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
            if(socket.isConnected()){// if the conncetion is successful then we start a listener thread
            
                listener = new ClientListener(in);
                listener.start();
                System.out.println("Listener activated...");
            }
         
            String fromClient = "";
            while(listener.isAlive() &&  (fromClient = sc.nextLine())!=null){
              
                if(listener.isAlive()){ // check if the listener thread is alive
                    out.println(fromClient);
                    out.flush();
                }

            }
        } catch (IOException ex) {
            System.out.println("It looks likes the server unexpected crash");
            //out.close();
           // ex.printStackTrace();
        }
        finally{
            try {
                if (socket != null)
                    System.out.println("Closing socket");
                    out.close();
                    socket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        System.out.println("Exiting");
    }
}
