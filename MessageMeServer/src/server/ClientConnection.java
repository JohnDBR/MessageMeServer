/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 *
 * @author john
 */
public class ClientConnection extends Thread {

    protected Server server = null;
    protected Socket clientSocket = null;
    protected String serverText = null;
    protected BufferedReader input = null;
    protected PrintWriter output = null;

    protected String user;

    public ClientConnection(Socket clientSocket, Server server) throws IOException {
        this.clientSocket = clientSocket;
        this.server = server;
        input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        output = new PrintWriter(clientSocket.getOutputStream(), true);
        this.serverText = "MessageMe Server"; //Need to be used!
        user = "Connected";
    }

    public void run() {
        long time = System.currentTimeMillis();
        //output.println(("ClientProcessor: " + this.serverText + " - " + time + "").getBytes()); //HTTP/1.1 200 OK\n\n
        System.out.println("Connection processed: " + time);
        String message;
        try {
            while (true) {
                message = input.readLine();
                if (message == null) {
                    close();
                    break;
                }
                readMessageOfClient(message);
            }
        } catch (IOException e) {
            System.out.print(e.getMessage());
        }
    }

    public void readMessageOfClient(String message) {
        String[] fields = message.split("-");
        if (fields[0].equals("Login")) {
            try {
                if (server.master.authenticate(fields[1], fields[2])) {
                    user = fields[1];
                    sendMessageToClient("Login Successfully");
                } else {
                    sendMessageToClient("Login Unsuccessfully");
                }
            } catch (Exception e) {
                System.out.println("Server busy, wait");
            }
        } else if (fields[0].equals("SignUp")) {
            int result = server.master.addUser(fields[1], fields[2], fields[3], fields[4]);
            if (result == 1) {
                user = fields[1];
            }
            sendMessageToClient(result + "");
        } else if (fields[0].equals("Close")) {
            try {
                server.disconnect(this);
            } catch (Exception e) {
                System.out.println("Server busy, wait");
            }
        } else if (fields[0].contains("ChatMessage")) {
            try {
                server.broadcast(this, fields[2], fields[1] + "-" + fields[3]);
            } catch (Exception e) {
                System.out.println("Server busy, wait");
            }
        }
    }

    public void sendMessageToClient(String message) {
        output.println(message);
    }

    public void close() {
        try {
            if (input != null) {
                input.close();
            }
            if (output != null) {
                output.close();
            }
            if (clientSocket != null) {
                clientSocket.close();
            }
            try {
                server.disconnect(this);
            } catch (Exception e) {
                System.out.println("Server busy, wait");
            }
        } catch (IOException e) {
            System.out.print(e.getMessage());
        }
    }
}
