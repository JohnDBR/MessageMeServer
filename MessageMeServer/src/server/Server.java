/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import java.util.LinkedList;
import server.controllers.Master;

/**
 *
 * @author john
 */
public class Server implements Runnable {

    protected int serverPort = 8080;
    protected ServerSocket serverSocket = null;
    protected boolean isStopped = false;
    protected Thread runningThread = null;

    protected LinkedList<ClientConnection> connections;

    protected Master master;

    public Server(int port) {
        this.serverPort = port;
        this.connections = new LinkedList<>();
        master = new Master();
    }

    public void run() {
        System.out.println("Starting server.");
        synchronized (this) {
            this.runningThread = Thread.currentThread();
        }
        openServerSocket();
        while (!isStopped()) {
            Socket clientSocket = null;
            try {
                clientSocket = this.serverSocket.accept();
            } catch (IOException e) {
                if (isStopped()) {
                    System.out.println("Server Stopped.");
                    return;
                }
                throw new RuntimeException("Error accepting client connection", e);
            }
            try {
                connections.add(new ClientConnection(clientSocket, this));
                connections.getLast().start();
            } catch (IOException ex) {
                System.out.println("Error processing client");
            }
        }
        System.out.println("Server Stopped.");
    }

    private synchronized boolean isStopped() {
        return this.isStopped;
    }

    public synchronized void stop() {
        this.isStopped = true;
        try {
            this.serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException("Error closing server", e);
        }
    }

    private void openServerSocket() {
        try {
            this.serverSocket = new ServerSocket(this.serverPort);
            System.out.println("Server is listening on " + serverPort);
        } catch (IOException e) {
            throw new RuntimeException("Cannot open port 8080", e);
        }
    }

    public synchronized void disconnect(ClientConnection client) {
        connections.remove(client);
        String s = "";
    }

    public synchronized void broadcast(ClientConnection activeClient, String user, String message) {
        for (ClientConnection client : connections) {
            //if (!client.equals(activeClient)) {
            //    client.sendMessageToClient(message);
            //}
            //if (client.user.equalsIgnoreCase(user)) {
            if (user.equalsIgnoreCase(client.user)) {
                client.sendMessageToClient(message);
                break; //test
            }
        }
    }

    public synchronized void broadcastToUsers(String users, String message) {
        for (ClientConnection client : connections) {
            if (users.contains(client.user.toUpperCase())) {
                client.sendMessageToClient(message);
            }
        }
    }

    public synchronized boolean userConnected(String user) {
        for (ClientConnection client : connections) {
            if (user.equalsIgnoreCase(client.user)) {
                return true;
            }
        }
        return false;
    }

    public synchronized String getUserOnlineFriends(String friends) {
        String onlineFriends = "OnlineFriends|";
        if (!friends.equals("NONE")) {
            String[] userFriends = friends.split("\\|");
            for (int i = 1; i < userFriends.length; i++) {
                String[] fields = userFriends[i].split("\\-");
                if (Boolean.valueOf(fields[1])) {
                    if (userConnected(fields[0])) {
                        onlineFriends = onlineFriends.concat(fields[0]);
                    }
                }
            }
        }
        if (onlineFriends.equals("OnlineFriends|")) {
            onlineFriends = "NONE";
        }
        return onlineFriends;
    }

}
