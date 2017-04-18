/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author john
 */
public class Client {

    private final int PORT = 9000; //Port for connection
    private final String HOST = "localhost"; //Host for connection (ip of the server)

    private String serverMessage; //Incoming messages from server
    private Socket clientSocket;
    private ServerSocket serverSocket;
    private DataOutputStream serverOut, clientOut;

    public Client() throws IOException {
        clientSocket = new Socket(HOST, PORT);

        clientOut = new DataOutputStream(clientSocket.getOutputStream());
        BufferedReader entry = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        while ((serverMessage = entry.readLine()) != null) { //Show server messages
            System.out.println(serverMessage);
        }
    }

}
