
import java.util.ArrayList;

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
        int delay = 0;
        String[] str = new String[1];
        if (args.length > 1) {
            str[0] = args[1];
        }

        ArrayList<Thread> threads = new ArrayList();
        for (int i = 0; i < nr; i++) {
            Thread.sleep(40);
            Runnable r = () -> {
                new TestClient(str);
            };
            Thread t = new Thread(r);
            threads.add(t);
            t.start();
        }
    }

}
