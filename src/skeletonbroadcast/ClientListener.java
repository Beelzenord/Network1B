/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package skeletonbroadcast;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author fno
 */
public class ClientListener extends Thread{
    private BufferedReader in ;
    public ClientListener(BufferedReader incomingStream){
        this.in = incomingStream;
    }

    @Override
    public void run() {
       
            try {
                 while(true){
                     String toClient = in.readLine();
                     System.out.println(toClient);
                     if(toClient==null){
                         System.out.println("BREAK OFF");
                         break;
                     }
                 }
                
            } catch (IOException ex) {
                Logger.getLogger(ClientListener.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        
    }
    
    
}
