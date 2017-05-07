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
        int result;
        switch (fields[0]) {
            case "Login":
                try {
                    if (server.master.authenticate(fields[1], fields[2])) {
                        user = fields[1];
                        sendMessageToClient("Login Successfully");
                        sendMessageToClient(server.master.getUserFriends(user));
                    } else {
                        sendMessageToClient("Login Unsuccessfully");
                    }
                } catch (Exception e) {
                    System.out.println("Server busy, wait");
                }
                break;
            case "SignUp":
                result = server.master.addUser(fields[1], fields[2], fields[3], fields[4]);
                if (result == 1) {
                    user = fields[1];
                }
                sendMessageToClient(result + "");
                break;
            case "Close":
                try {
                    server.disconnect(this);
                } catch (Exception e) {
                    System.out.println("Server busy, wait");
                }
                break;
            case "ChatMessage":
                try {
                    server.broadcast(this, fields[2], message);
                } catch (Exception e) {
                    System.out.println("Server busy, wait");
                }
                break;
            case "FriendRequest":
                try {
                    result = server.master.friendOrRequestProcessor(fields[1].toUpperCase(), fields[2].toUpperCase(), Boolean.valueOf(fields[3]));
                    String message1, message2;
                    switch (result) {
                        case 0:
                            server.broadcast(this, fields[1], "Problem or invalid Request!");
                            break;
                        case 1:
                            message1 = "NewFriend-" + fields[2].toUpperCase();
                            message2 = "NewFriend-" + fields[1].toUpperCase();
                            server.broadcast(this, fields[1], message1);
                            server.broadcast(this, fields[2], message2);
                            break;
                        case 4:
                            message1 = "DeleteFriend-" + fields[2].toUpperCase();
                            message2 = "DeleteFriend-" + fields[1].toUpperCase();
                            server.broadcast(this, fields[1], message1);
                            server.broadcast(this, fields[2], message2);
                            break;
                        case 3:
                            server.broadcast(this, fields[1], "Already friends!");
                            break;
                        case 5:
                            server.broadcast(this, fields[1], "Request successfully sent");
                            server.broadcast(this, fields[2], "FriendRequest-" + fields[1].toUpperCase());
                            break;
                    }
                } catch (Exception e) {
                    System.out.println("Server busy, wait");
                }
                break;
            default:
                break;
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
