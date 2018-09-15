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
import java.net.SocketTimeoutException;
import java.util.Iterator;
import java.util.Timer;

/**
 *
 * @author fno
 * 
 * Server thread that handles client request including broadcasting messages.
 * also handles disconnection from the client side
 * 
 * parts of the code is from "Object-Oriented Software Development Using Java" course book
      *     by author Xiaoping Jia chapter 12. Parts used were in multithreading and broadcasting.
 * 
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
    
    /**
     * Uses the servers sockets for bi-directional communication and
     * the id for uniquely identifying the user.
     * @param incoming
     * @param id 
     */
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
                try {
                    if (in != null)
                        in.close();
                    if (out != null)
                        out.close();
                } catch (IOException e) {
                }
                System.out.println("Could not establish connection to client");
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
    /**Run the thread concurrently
     * 
     * 
    */
    @Override
    public void run() {
        if (in != null && out != null) {
            sendMessage("Hello! Welcome to the chat service!");
            String str = "";
            try {
                while (!clientWantsOut) {
                    str = in.readLine();

                    if (str.length() > 0 && str.charAt(0) == '/') {
                        serverCommands(str.trim().substring(1).toUpperCase());
                    } 
                    else {
                        if (str.length() != 0 && (!str.equals(""))) {
                            doBroadcast("[" + this.eitherIdOrNickname() + "] " + str);
                        }
                    }
                }
                sendMessage("BYE");

            } catch (SocketTimeoutException ex) {
                System.out.println("A client timed out");
            } catch (NullPointerException ex) {
                System.out.println("Connection with client abrupty lost");
            } catch (SocketException se) {
                System.out.println("Could not read from socket handeling client");
            } catch (IOException ex) {
                System.out.println("Client handler run method");
            } finally {
                try {
                    System.out.println("Removing client");
                    doBroadcast("User: " + this.eitherIdOrNickname() + " disconnected");
                    Server.removeClient(this);
                    if (incoming != null) {
                        incoming.close();
                    }
                    if (in != null) {
                        in.close();
                    }
                    if (out != null) {
                        out.close();
                    }
                } catch (IOException ex) {
                    System.out.println("could not close in stream in client handler");
                } catch (java.util.ConcurrentModificationException ex) {
                    System.out.println("Too many clients disconnecting simutainiously causing concurrency issues, "
                            + "thread will close in a few minutes.");;
                }
            }
        }
    }
    /**
     * if the user doesn't have a nickname we call it by the ID.
     * 
     **/
    private String eitherIdOrNickname() {
        if (this.getNickName() == null) {
            return Integer.toString(this.getUserId());
        }
        return this.getNickName();
    }
     /**
     * If the user sends a string beginning with '/', the method below
     * handles the command.
     * @param string subSequence
     **/
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
                    if (received.length <= 1) {
                        sendMessage("error: you have inserted no argument");
                    } else {
                        nameClient(received[1]);
                    }
                    break;
                case "HELP":
                    sendInstructions();
                    break;
                default:
                    sendMessage("error: command not recognized");
                    break;
            }
        } catch (ArrayIndexOutOfBoundsException ex) {
            sendMessage("error: command not recognized");
        }
    }

    /**
     * takes the Hashset from server main and converts it to an iterator so that
     * we can have each live thread to send a message to its corresponding
     * client, hence the broadcast.
     *  code is from "Object-Oriented Software Development Using Java" course book
      *     by author Xiaoping Jia chapter 12.
      * @param string 
     */
    private synchronized void doBroadcast(String string) {
        Iterator iter = Server.getIterableOfClients();
        while (iter.hasNext()) {
            BroadcastClientHandler t = (BroadcastClientHandler) iter.next();
            if (t != this) {
                if (t != null) {
                    t.sendMessage(string);
                }
            }
        }
    }

    /**
     * Takes the iterator from main server thread, finds out who's connected,
     * and send it to the user.
     *
     */
    private void getAllConnectedClients() {
        Iterator iter = Server.getIterableOfClients();
        while (iter.hasNext()) {
            BroadcastClientHandler t = (BroadcastClientHandler) iter.next();
            String tmp = null;
            tmp = "[User " + Integer.toString(t.getUserId()) + "]";
            if (t.getNickName() != null) {
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
    /**
     * sends instructions to the user.
     */
    private void sendInstructions() {
        String instructions = "Instructions\n(1)'/who' provides a list of all connected users"
                + "\n(2)'/quit', terminate your connection"
                + "\n(3)'/name' change nickname"
                + "\n(4)'/help', provides instructions";
        sendMessage(instructions);
    }
    /**
     * Prevents client from using an already existing name.
     * @param newNickName 
     */
    private void nameClient(String newNickName) {
        Iterator iter = Server.getIterableOfClients();
        boolean nameAlreadyExist = false;
        while (iter.hasNext()) {
            BroadcastClientHandler t = (BroadcastClientHandler) iter.next();
            if (t.nickName != null) {
                if (t.nickName.equals(newNickName)) {
                    nameAlreadyExist = true;
                }
            }
        }
        if (nameAlreadyExist) {
            sendMessage("Another user has already taken this name, please choose another");
        } else {
            this.nickName = newNickName;
            sendMessage("You've changed you're name to " + this.nickName);
        }
    }

}
