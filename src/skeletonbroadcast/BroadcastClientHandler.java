/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package skeletonbroadcast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author fno
 */
public class BroadcastClientHandler extends Thread{
    protected Socket incoming;
    protected int id;
    protected BufferedReader in;
    protected PrintWriter out;
    protected boolean clientWantsOut;
    
    protected ObjectOutputStream os;
    protected String nickName;
    public BroadcastClientHandler(Socket incoming, int id){
        this.clientWantsOut = false;
        this.incoming = incoming;
        this.id = id;
        
        if(incoming!=null){
            try {
                in = new BufferedReader(new InputStreamReader(incoming.getInputStream()));
                out = new PrintWriter(new OutputStreamWriter(incoming.getOutputStream()));
                
            } catch (IOException ex) {
                Logger.getLogger(BroadcastClientHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    /**
     * sends a message to client
     */
    public synchronized void sendMessage(String msg){
        if(out!=null){
            out.println(msg);
            out.flush();
        }
    }

    @Override
    public void run() {
        if(in!=null && out!=null){
            sendMessage("Hello! This Java BroadcastEchoServer");
            sendMessage("Enter BYE to exit");
                try {
                    while(!clientWantsOut){
                        String str =  in.readLine();
                       
                        if(str==null){ 
                            break;
                        }
                        else if(str.equals("")){
                            
                        }
                        else if(str.charAt(0)== '/'){
                           
                            serverCommands(str.trim().substring(1).toUpperCase());
                            System.out.println("Command");
                        }
                        else{
                            sendMessage("Echo: " + str);
                            if(str.trim().toUpperCase().equals("BYE")){
                                break;
                            }
                            else{
                                doBroadcast("Broadcast(" + id + "): " +str);
                          }
                        }
                    }
                    sendMessage("[from Server]=> BYE");
                    doBroadcast("User: " + id + " signing off");
                    incoming.close();
                    Server.activeClients.remove(this);
                } catch (IOException ex) {
                    Logger.getLogger(BroadcastClientHandler.class.getName()).log(Level.SEVERE, null, ex);
                }
            
        }
    }
    /*Server specific commands
    * 
    */
    private void serverCommands(String subSequence) {
        
        switch(subSequence){
            case "QUIT": clientWantsOut=true;break;
            case "WHO":getAllConnectedClients();System.out.println("who is there");break;
            case "NAME": break;
            default: sendMessage("Command not recognized");break;
        }
        
    }
    /**
     * takes the Hashset from server main and converts it to an iterator
     * so that we can have each live thread to send a message to its
     * corresponding client, hence the broadcast.
     */
    private void doBroadcast(String string) {
       Iterator iter = Server.getIterableOfClients();//Server.activeClients.iterator();
       while(iter.hasNext()){
           BroadcastClientHandler t = (BroadcastClientHandler) iter.next();
           if(t!=this){
             t.sendMessage(string);
           }
       }
    }

   
     /**
     * Takes the iterator from main server thread, finds out who's connected,
     * and send it to the user
     * 
     */
    private void getAllConnectedClients() {
        //To change body of generated methods, choose Tools | Templates.
         Iterator iter = Server.getIterableOfClients();
         
         while(iter.hasNext()){
              BroadcastClientHandler t = (BroadcastClientHandler) iter.next();
              if(t==this){
                  sendMessage(this.id+ "(you)");
              }
              else{
                  sendMessage(Integer.toString(t.id));
              }
              
         }
    }
    
}

