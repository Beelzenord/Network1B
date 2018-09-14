/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
//package skeletonbroadcast;

/**
 *
 * @author Niklas
 */
public class Tester {
    public static void main(String[] args) throws InterruptedException {
        int nr = Integer.parseInt(args[0]);
        for (int i = 0; i < nr; i++) {
            Thread.sleep(25);
            Runnable r = () -> {
                new ClientTester(new String[0]);
            };
            new Thread(r).start();
        }
    }

}
