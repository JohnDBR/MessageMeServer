/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 *
 * @author john
 */
public class main {

    public static void main(String[] args) {
        Server server = new Server(9000);
        new Thread(server).start(); //Running on! http://localhost:9000/

        try { //Just giving time for the server to load...
            Thread.sleep(20);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        BufferedReader read = new BufferedReader(new InputStreamReader(System.in));
        int op;
        
        System.out.println("Press 1 to stop the server.");
        do {
            try {
                op = Integer.valueOf(read.readLine());
            } catch (Exception e) {
                System.out.println("Error, please enter a valid option.");
                op = 2;
            }
        } while (op != 1);
        
        System.out.println("Stopping Server");
        server.stop();
    }
}
