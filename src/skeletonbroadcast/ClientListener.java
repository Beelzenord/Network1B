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
 * @author fno
 * this class displays text to the user asynchronously while the main
 * Client thread may write to it
 */
public class ClientListener extends Thread{
    private BufferedReader in ;
    private long serverThreadID;
    public ClientListener(BufferedReader incomingStream){
        this.in = incomingStream;
    }

    @Override
    public void run() {
      
            try {
                  System.out.println(in.readLine());
                  
                 // serverThreadID = Long.parseLong(in.readLine().trim());
                  String toClient="";
                 // while((toClient = in.readLine())!=null){
                  while(true){
                      toClient = in.readLine();
                     System.out.println(((String) toClient).trim());   
                     if(toClient.equals("BYE")){
                         System.out.println("BREAK OFF");
                         break;
                     }
                    
                     
                 }
                System.out.println("terminating");
            } catch (IOException ex) {
                System.out.println("Input/output error");
             //   Logger.getLogger(ClientListener.class.getName()).log(Level.SEVERE, null, ex);
            }
              catch(NullPointerException ex)  {
                  System.out.println("Failure, unable to hear from server...");
              }
            
            finally {
	            try {
	                if (in != null) {
	                    in.close();
                            System.out.println("Closing incoming stream buffer");
	                }
	            } catch (IOException ex) {
	                System.out.println("could not close incomingStream");
	            }
	        }
    }
    
    
}
