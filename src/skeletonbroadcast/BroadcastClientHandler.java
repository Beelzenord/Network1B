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
           
             String str = "";
            try {
                //!clientWantsOut ||
                while (true ) {
                   str = in.readLine();
                   
                   
                    
                    if (str == null) {
                        System.out.println("We may have lost a client");
                        break;
                    }
                    
                    else if (str.length()>0 &&str.charAt(0) == '/') {
                        serverCommands(str.trim().substring(1).toUpperCase());
                        System.out.println("Command");
                    } else {
 
                       if(str.length()!=0 && (!str.equals(""))){
                           //sendMessage("Echo: " + str);
                           doBroadcast("Broadcast(" + id + "): " + str);
                         }
                        
                    }
                }
                sendMessage("[from Server]=> BYE");
                doBroadcast("User: " + id + " signing off");
                incoming.close();
                Server.activeClients.remove(this);
            } 
            
            catch(SocketException se){
                System.out.println("problem with server");
            }
            catch (IOException ex) {
                System.out.println("Client handler run method");
                
                ex.printStackTrace();
            } finally {
                try {
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
                    nameClient(received[1]);
                    break;
                case"HELP":sendInstructions();break;
                default:
                    sendMessage("Command not recognized");
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
            if (t == this) {
                sendMessage(this.id + nickName);
            } else {
                sendMessage(Integer.toString(t.id) + " " + t.getNickName());
            }


        }
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

