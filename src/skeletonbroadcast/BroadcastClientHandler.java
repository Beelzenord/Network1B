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
    protected UserInfo currentUser;
    protected ObjectOutputStream os;
    protected String nickName;
    public BroadcastClientHandler(Socket incoming, int id){
        this.clientWantsOut = false;
        this.incoming = incoming;
        this.id = id;
        currentUser = new UserInfo(id);
        if(incoming!=null){
            try {
                in = new BufferedReader(new InputStreamReader(incoming.getInputStream()));
                out = new PrintWriter(new OutputStreamWriter(incoming.getOutputStream()));
                //outListClientStream = new 
              //  os = new ObjectOutputStream(incoming.getOutputStream());
            } catch (IOException ex) {
                Logger.getLogger(BroadcastClientHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
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
                    System.out.println("Client handler run method");
                    ex.printStackTrace();
                } finally {
                    try {
                        if (in != null)
                            in.close();
                        if (out != null)
                            out.close();
                    } catch (IOException ex) {
                        System.out.println("could not close in stream in client handler");
                    } 
                }
            
        }
    }

    private void serverCommands(String subSequence) {
        String[] received = subSequence.split(" ");
        try {
            switch(received[0]){
                case "QUIT": 
                    clientWantsOut=true;
                    break;
                case "WHO":
                    System.out.println("who is there");
                    break;
                case "NAME": 
                    this.nickName = received[1];
                    break;
                default: 
                    sendMessage("Command not recognized");
                    break;
            }
        } catch (ArrayIndexOutOfBoundsException ex ){
            ex.printStackTrace();
        }
        
    }
    

    private void doBroadcast(String string) {
       Iterator iter = Server.activeClients.iterator();
       while(iter.hasNext()){
           BroadcastClientHandler t = (BroadcastClientHandler) iter.next();
           if(t!=this){
             t.sendMessage(string);
           }
       }
    }

    public UserInfo getCurrentUser() {
        return this.currentUser;
    }

    
    
    private synchronized void sendObject() {
        //To change body of generated methods, choose Tools | Templates.
        ArrayList<String> users = new ArrayList<>();
        Iterator iter = Server.activeClients.iterator();
       while(iter.hasNext()){
           BroadcastClientHandler t = (BroadcastClientHandler) iter.next();
           if(t==this){
               users.add(Integer.toString(id)+ "(you)");
           }
           else{
                users.add(Integer.toString(t.id));   
           }
       }
       if(out!=null){
           out.println(users);
           out.flush();
       }
    }
    
}
