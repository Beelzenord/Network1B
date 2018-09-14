/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
//package skeletonbroadcast;

import java.io.IOException;
import java.net.Socket;
import java.util.TimerTask;

/**
 *
 * @author Niklas
 */
public class TestTask extends TimerTask {
    private Socket socket;

    public TestTask(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            if (socket != null)
            socket.close();
        } catch (IOException ex) {
            System.out.println("lul did close socket");
        }
    }

    
    
    
}
