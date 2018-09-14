/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
//package skeletonbroadcast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Timer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author fno
 */
public class BroadcastClientHandler extends Thread {

    protected Socket incoming;
    protected int id;
    protected BufferedReader in;
    protected PrintWriter out;
    protected boolean clientWantsOut;

    protected ObjectOutputStream os;
    protected String nickName;
    protected Timer sendAlive;
    public BroadcastClientHandler(Socket incoming, int id) {
        this.clientWantsOut = false;
        this.incoming = incoming;
        this.id = id;
         sendAlive = new Timer();
        if (incoming != null) {
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
    public synchronized void sendMessage(String msg) {
        if (out != null) {
            out.println(msg);
            out.flush();
        }
    }

    @Override
    public void run() {
        if (in != null && out != null) {
            sendMessage("Hello! Welcome to the chat service!");
            //sendMessage
           // System.out.println(this.getId());
          //  sendMessage(Long.toString(this.getId()));
             String str = "";
            try {
                //!clientWantsOut ||
                while (!clientWantsOut ) {
                   str = in.readLine();
                   
                   
                    
                  /*  if (str == null) {
                       // throw new NullPointerException("We may have lost a client");
                       // System.out.println("We may have lost a client");
                       // break;
                    }*/
                    
                     if (str.length()>0 &&str.charAt(0) == '/') {
                        /*if(str.trim().substring(1)==null){
                            
                        }*/
                        serverCommands(str.trim().substring(1).toUpperCase());
                       
                    } else {
 
                       if(str.length()!=0 && (!str.equals(""))){
                           //sendMessage("Echo: " + str);
                           doBroadcast("["+this.eitherIdOrNickname()+"] " + str);
                         }
                        
                    }
                }
                sendMessage("BYE");
                
            } 
            catch(NullPointerException ex){
                System.out.println("Connection with client abrupty lost");
            }
            catch(SocketException se){
                System.out.println("problem with server");
            }
            catch (IOException ex) {
                System.out.println("Client handler run method");
                
                ex.printStackTrace();
            } 
            
          
            finally {
                try {
                    System.out.println("Client down");
                    doBroadcast("User: " + this.eitherIdOrNickname() + " disconnected");
                    incoming.close();
                    Server.activeClients.remove(this);
                    if (in != null) {
                        in.close();
                    }
                    if (out != null) {
                        out.close();
                    }
                } catch (IOException ex) {
                    System.out.println("could not close in stream in client handler");
                }
            }

        }
    }
    private String eitherIdOrNickname(){
        if(this.getNickName()==null){
            return Integer.toString(this.getUserId());
        }
        return this.getNickName();
    }
    /*Server specific commands
    * 
     */
    private void serverCommands(String subSequence) {
        String[] received = subSequence.split(" ");
        try {
            switch (received[0]) {
                case "QUIT":
                    clientWantsOut = true;
                    break;
                case "WHO":
                    getAllConnectedClients();
                    break;
                case "NAME":
                    //this.nickName = received[1];
                    if(received.length<=1){
                    //System.out.println(received.length + " " + Arrays.toString(received));
                        sendMessage("error: you have inserted no argument");
                    }
                    else{
                        nameClient(received[1]);
                    }
                    
                    break;
                case"HELP":sendInstructions();break;
                default:
                    sendMessage("error: command not recognized");
                    break;
            }
        } catch (ArrayIndexOutOfBoundsException ex) {
            ex.printStackTrace();
        }

    }

    /**
     * takes the Hashset from server main and converts it to an iterator so that
     * we can have each live thread to send a message to its corresponding
     * client, hence the broadcast.
     */
    private void doBroadcast(String string) {
        
        Iterator iter = Server.getIterableOfClients();//Server.activeClients.iterator();
        while (iter.hasNext()) {
            BroadcastClientHandler t = (BroadcastClientHandler) iter.next();
            if (t != this) {
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

        while (iter.hasNext()) {
            BroadcastClientHandler t = (BroadcastClientHandler) iter.next();
            String tmp = null;
          /*  if(t==this){
                tmp = "[User "+Integer.toString(this.getUserId())+ "]";
                if(this.nickName!=null){
                    tmp += " " + this.nickName; 
                }
                
            }
            else{
                   tmp = "[User "+Integer.toString(t.getUserId())+ "]";
                if(t.getNickName()!=null){
                    tmp += " " + t.getNickName(); 
                }
                
                
            }*/
             tmp = "[User "+Integer.toString(t.getUserId())+ "]";
                if(t.getNickName()!=null){
                    tmp += " " + t.getNickName(); 
                }
             sendMessage(tmp);
           
        }
    }

    public int getUserId() {
        return id;
    }
    
    public String getNickName() {
        return nickName;
    }

    private void sendInstructions() {
        String instructions= "Instructions\n(1)'/who' provides a list of all connected users"
                + "\n(2)'/quit', terminate your connection"
                + "\n(3)'/name' change nickname"
                + "\n(4)'/help', provides instructions";
        sendMessage(instructions);
    }

    private void nameClient(String newNickName) {
           Iterator iter = Server.getIterableOfClients();
           boolean nameAlreadyExist = false;
           while(iter.hasNext()){
                BroadcastClientHandler t = (BroadcastClientHandler) iter.next();
                if(t.nickName!=null){
                    if(t.nickName.equals(newNickName)){
                        nameAlreadyExist = true;
                    }
                }
           }
           if(nameAlreadyExist){
               sendMessage("Another user has already taken this name, please choose another");
           }
           else{
               this.nickName = newNickName;
               sendMessage("You've changed you're name to " + this.nickName);
           }
    }

}

