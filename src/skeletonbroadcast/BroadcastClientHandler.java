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
    public BroadcastClientHandler(Socket incoming, int id){
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
                    while(true){
                        String str =  in.readLine();
                       
                        if(str==null){                  
                            break;
                        }
                        else{
                            sendMessage("Echo: " + str);
                            if(str.trim().equals("BYE")){
                                break;
                            }
                            else{
                                Iterator iter = Server.activeClients.iterator();
                            while(iter.hasNext()){
                                BroadcastClientHandler t = (BroadcastClientHandler) iter.next();
                                if(t!=this){
                                    t.sendMessage("Broadcast(" + id + "): " + str);
                                }
                            }
                         }
                        }
                    }
                    incoming.close();
                    Server.activeClients.remove(this);
                } catch (IOException ex) {
                    Logger.getLogger(BroadcastClientHandler.class.getName()).log(Level.SEVERE, null, ex);
                }
            
        }
    }
    
}
