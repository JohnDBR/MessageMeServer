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

    public ClientConnection(Socket clientSocket, Server server) throws IOException {
        this.clientSocket = clientSocket;
        this.server = server;
        input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        output = new PrintWriter(clientSocket.getOutputStream(), true);
        this.serverText = "MessageMe Server";
    }

    public void run() {
        long time = System.currentTimeMillis();
        //output.println(("ClientProcessor: " + this.serverText + " - " + time + "").getBytes()); //HTTP/1.1 200 OK\n\n
        System.out.println("Request processed: " + time);
        String message;
        try {
            while (true) {
                message = input.readLine();
                if (message == null) {
                    close();
                    break;
                }
                readMessage(message);
            }
        } catch (IOException e) {
            System.out.print(e.getMessage());
        }
    }

    public void readMessage(String message) {
        server.broadcast(this, message);
    }

    public void sendMessage(String message) {
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
            server.disconnect(this);
        } catch (IOException e) {
            System.out.print(e.getMessage());
        }
    }
}
