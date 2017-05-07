/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import client.forms.ChatForm;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author john
 */
public class ConnectionTest {

    public static void main(String[] args) {
        //ChatForm cf = new ChatForm();

        //Just a few test
        /*Thread client1Thread = new Thread(() -> {
            try {
                Client client1 = new Client();
                // Thread.sleep(1000);
                client1.sendMessage("test client1");
                client1.close();
            } catch (Exception e) {
                System.out.println("fallo 1?");
            }
        });
        client1Thread.start();
        //Thread Client2Thread = new Thread(() -> {
        try {
            Client client2 = new Client();
            Thread.sleep(1000);
            String s = client2.receiveMessage();
            System.out.println("Messsage:" + s);
            //if ( == "test") {
            //    System.out.println("Working! message received on client 2");
            //}
            client2.close();
        } catch (Exception e) {
            System.out.println("Just in case!");
        }*/
        //});
        //Client2Thread.start();
    }
}
