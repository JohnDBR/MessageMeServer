/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 *
 * @author john
 */
public class Client {

    private final int PORT = 9000; //Port for connection
    private final String HOST = "localhost"; //Host for connection (ip of the server)

    private Socket clientSocket;
    private Scanner input;
    private PrintWriter output;

    private String user;

    public Client() {
        try {
            clientSocket = new Socket(HOST, PORT);
            input = new Scanner(new InputStreamReader(clientSocket.getInputStream()));
            output = new PrintWriter(clientSocket.getOutputStream(), true);
            user = "connected";
        } catch (IOException ex) {
            System.out.println("Unable to connect to server.");
        }
    }

    public void close() {
        try {
            sendMessage("Close");
            if (input != null) {
                input.close();
            }
            if (output != null) {
                output.close();
            }
            if (clientSocket != null) {
                clientSocket.close();
            }
        } catch (IOException e) {
            System.out.print(e.getMessage());
        }
    }

    public String receiveMessage() throws IOException {
        return input.nextLine();
    }

    public void sendMessage(String message) throws IOException {
        output.println(message);
    }

    //GETTERS SETTERS
    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

}
