/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.controllers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author john
 */
public class Master {

    private String routeUser;
    private String routeFriends;
    private String routeFriendRequests;
    private String routeMessages;
    private int Modo;

    private ArrayList<String[]> userFile;
    private Map<String, Boolean> friends;

    public Master() {
        routeUser = "./database/Users.txt";
        routeFriends = "./database/Friends.txt";
        routeFriendRequests = "./database/FriendRequests.txt";
        routeMessages = "./database/Messages.txt";
        loadData();

        //encryptDecryptFile(routeUser, true);
        //encryptDecryptFile(routeFriends, true);
    }

    //file methods
    private void loadData() {
        if (checkFile(routeUser) && checkFile(routeFriends) && checkFile(routeFriendRequests)) {
            setModo(2);
            userFile = getUserFile();
            String s = "";

            loadFriendsFiles();
        } else {
            System.out.println("Error, database corrupted!");
        }
    }

    private void loadFriendsFiles() {
        friends = new HashMap<String, Boolean>();
        getFriendsFile(false);
        getFriendsFile(true);
    }

    public synchronized boolean checkFile(String route) {
        File file = new File(route);
        return file.exists();
    }

    public void encryptDecryptFile(String route, boolean encript) {
        try {
            File f = new File(route);
            File mod = new File("./database/mod.txt");
            mod.createNewFile();

            FileReader fr = new FileReader(f);
            BufferedReader br = new BufferedReader(fr);

            FileWriter fw = new FileWriter(mod);
            PrintWriter pr = new PrintWriter(fw);

            if (encript) {
                setModo(1);
            } else {
                setModo(2);
            }

            String linea;
            String linea2 = "";
            while ((linea = br.readLine()) != null) {
                String[] fields = linea.split("\\|");

                linea2 = "";
                if (encript) {
                    for (String field : fields) {
                        linea2 = linea2.concat(Morse(Rotk(field)) + "|");
                    }
                } else {
                    for (String field : fields) {
                        linea2 = linea2.concat(Rotk(Morse(field)) + "|");
                    }
                }

                pr.println(linea2);
            }

            pr.close();
            fw.close();
            br.close();
            fr.close();

            boolean delete;
            boolean rename;
            do {
                delete = f.delete();
                rename = mod.renameTo(f);
                System.out.println(delete + " " + rename);
            } while (!(delete && rename));
        } catch (Exception ex) {
            System.out.println("Error en base de datos");
        }
    }

    //user methods
    public synchronized boolean authenticate(String user, String password) {
        String level;
        for (String[] userOfFile : userFile) {
            user = user.toUpperCase(); //extra //fix the thing about uppercases
            password = password.toUpperCase(); //extra
            setModo(2);
            if (user.equals(userOfFile[0]) && password.equals(userOfFile[1])) { //extra 
                level = userOfFile[2];
                //if (level.equals("0")) {
                //} else if (level.equals("1")) {
                //} else if (level.equals("2")) {
                //}
                return true;
            }
        }
        return false;
    }

    private ArrayList<String[]> getUserFile() { //YOU CAN READ THE FILE ONCE AND SAVE IT ON AN ARRAYLIST AND STOP OPENNING AND CLOSING THE FILE AGAIN AND AGAIN... BRUHH!
        ArrayList<String[]> UserFile = new ArrayList<>();

        try {
            File f = new File(routeUser);

            FileReader fr = new FileReader(f);
            BufferedReader br = new BufferedReader(fr);

            String line;
            br.readLine();
            while ((line = br.readLine()) != null) { //A hash will be amazing!
                String[] fields = line.split("\\|");
                for (int i = 0; i < fields.length; i++) {
                    String s = Rotk(Morse(fields[i]));
                    fields[i] = s;
                    //System.out.print(fields[i] + " ");
                }
                //System.out.println("");
                UserFile.add(fields);
            }

            br.close();
            fr.close();

            return UserFile;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public synchronized boolean userExist(String user) {
        for (String[] userFields : userFile) {
            if (userFields[0].equals(user)) {
                return true;
            }
        }
        return false;
    }

    public synchronized int addUser(String user, String password, String password1, String level) {
        if (validateWyN(user) && validateWyN(password) && validateWyN(password1) && validateNumber(level)) {
            if (password.equals(password1)) {
                if (addToUserFile(user, password, level)) {
                    String[] fields = {user, password, level};
                    userFile.add(fields);
                    return 1; //Successfully saved
                }
                return 3; //Unsuccessfully saved
            } else {
                return 2; //Different passwords
            }
        }
        return 0; //Bad input
    }

    public synchronized int deleteUser(String user) {
        if (validateWyN(user)) {
            int o = eraseToUserFile(user);
            if (o == 1) {
                for (String[] userOfFile : userFile) {
                    if (user.equals(userOfFile[0])) {
                        userFile.remove(userOfFile);
                    }
                }
            }
            return o; //1 Successfully erased, 0 Unsuccesfully erased
        }
        return 3; //Bad input
    }

    private boolean addToUserFile(String user, String password, String level) {
        try {

            setModo(1);

            int sw = 0;
            File f = new File(getRouteUser());
            File mod = new File("./database/mod.txt");
            mod.createNewFile();

            FileReader fr = new FileReader(f);
            BufferedReader br = new BufferedReader(fr);

            FileWriter fw = new FileWriter(mod);
            PrintWriter pr = new PrintWriter(fw);

            String linea;
            String user1 = Morse(Rotk(user));
            String linea2 = user1 + "|" + Morse(Rotk(password)) + "|" + Morse(Rotk(level)) + "|";
            while ((linea = br.readLine()) != null) {
                pr.println(linea);
                String[] fields = linea.split("\\|");
                if (fields[0].contains(user1)) {
                    sw = 1;
                }
            }
            if (sw == 0) {
                pr.println(linea2);
            }

            pr.close();
            fw.close();
            br.close();
            fr.close();

            /*
            boolean delete = f.delete();
            boolean rename = mod.renameTo(f);
            System.out.println(delete + " " + rename);
             */
            boolean delete;
            boolean rename;
            do {
                delete = f.delete();
                rename = mod.renameTo(f);
                //System.out.println(delete + " " + rename);
            } while (!(delete && rename));
            if (sw == 0) {
                return true;
            }
        } catch (Exception ex) {
            System.out.println("Error en base de datos");
        }
        return false;
    }

    private int eraseToUserFile(String user) {
        int success = 0;
        try {

            setModo(1);

            int sw = 0;
            File f = new File(getRouteUser());
            File mod = new File("./database/mod.txt");
            mod.createNewFile();

            FileReader fr = new FileReader(f);
            BufferedReader br = new BufferedReader(fr);

            FileWriter fw = new FileWriter(mod);
            PrintWriter pr = new PrintWriter(fw);

            String linea;
            String user1 = Morse(Rotk(user));
            while ((linea = br.readLine()) != null) {
                String[] fields = linea.split("\\|");
                if (!fields[0].equals(user1)) {
                    pr.println(linea);
                } else {
                    sw = 1;
                }
            }
            if (sw == 1) {
                success = sw;
            }

            pr.close();
            fw.close();
            br.close();
            fr.close();

            boolean delete;
            boolean rename;
            do {
                delete = f.delete();
                rename = mod.renameTo(f);
                //System.out.println(delete + " " + rename);
            } while (!(delete && rename));
        } catch (Exception ex) {
            success = 2;
            System.out.println("Error en base de datos");
        }
        return success;
    }

    //friends methods
    private void getFriendsFile(boolean request) {

        try {
            File f;
            if (request) {
                f = new File(routeFriendRequests);
            } else {
                f = new File(routeFriends);
            }

            FileReader fr = new FileReader(f);
            BufferedReader br = new BufferedReader(fr);

            String line;
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] fields = line.split("\\|");
                for (int i = 0; i < fields.length; i++) {
                    String s = Rotk(Morse(fields[i]));
                    fields[i] = s;
                    //System.out.print(fields[i] + " ");
                }
                //System.out.println("");
                if (request) {
                    friends.put(fields[0] + "-" + fields[1], Boolean.FALSE);
                } else {
                    friends.put(fields[0] + "-" + fields[1], Boolean.TRUE);
                }
            }

            br.close();
            fr.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized String getUserFriends(String user) {
        String friendsMessage = "Friends|";
        for (Map.Entry<String, Boolean> entry : friends.entrySet()) {
            String key = entry.getKey();
            boolean value = entry.getValue();
            if (key.contains(user.toUpperCase())) {
                String[] users = key.split("\\-");
                if (users[0].equalsIgnoreCase(user) && value) {
                    friendsMessage = friendsMessage.concat(users[1] + "-" + Boolean.toString(value) + "|");
                } else if (users[1].equalsIgnoreCase(user)) {
                    friendsMessage = friendsMessage.concat(users[0] + "-" + Boolean.toString(value) + "|");
                }
            }
            //System.out.println(key + "-" + value);
        }
        if (friendsMessage.equals("Friends|")) {
            friendsMessage = "NONE";
        }
        return friendsMessage;
    }

    public synchronized int friendOrRequestProcessor(String user, String user1, boolean action) {
        boolean bool;
        int o = 0; //Problem or invalid Request!
        if (userExist(user) && userExist(user1)) {
            if (friends.containsKey(user + "-" + user1)) {
                bool = friends.get(user + "-" + user1);
                if (!bool) {
                    if (action) {
                        friends.replace(user + "-" + user1, Boolean.TRUE);
                        if (addToFriendFile(user, user1, false) && eraseToFriendFile(user, user1, true)) {
                            o = 1; //Friend request accepted!
                        } else {
                            loadFriendsFiles();
                        }
                    } else {
                        friends.remove(user + "-" + user1);
                        if (eraseToFriendFile(user, user1, true)) {
                            o = 2; //Friend request denied!
                        } else {
                            loadFriendsFiles();
                        }
                    }
                } else if (action) {
                    o = 3; //The users are friends!  
                } else {
                    friends.remove(user + "-" + user1);
                    if (eraseToFriendFile(user, user1, false)) {
                        o = 4; //Friend deleted!
                    } else {
                        loadFriendsFiles();
                    }
                }
            } else if (friends.containsKey(user1 + "-" + user)) {
                bool = friends.get(user1 + "-" + user);
                if (!bool) {
                    if (action) {
                        friends.replace(user1 + "-" + user, Boolean.TRUE);
                        if (addToFriendFile(user1, user, false) && eraseToFriendFile(user1, user, true)) {
                            o = 1; //Friend request accepted!
                        } else {
                            loadFriendsFiles();
                        }
                    } else {
                        friends.remove(user1 + "-" + user);
                        if (eraseToFriendFile(user1, user, true)) {
                            o = 2; //Friend request denied!
                        } else {
                            loadFriendsFiles();
                        }
                    }
                } else if (action) {
                    o = 3; //The users are friends!  
                } else {
                    friends.remove(user1 + "-" + user);
                    if (eraseToFriendFile(user1, user, false)) {
                        o = 4; //Friend deleted!
                    } else {
                        loadFriendsFiles();
                    }
                }
            } else if (action) {
                friends.put(user + "-" + user1, Boolean.FALSE);
                if (addToFriendFile(user, user1, true)) {
                    o = 5; //Request successfully created!   
                }
            }
        } else {
            o = 6; //User doesn't exist!
        }

        if (o == 0) {
            loadFriendsFiles();
        }

        return o;
    }

    private boolean addToFriendFile(String user, String user1, boolean request) {
        try {

            setModo(1); //encript

            int sw = 0;
            File f;
            if (request) {
                f = new File(routeFriendRequests);
            } else {
                f = new File(routeFriends);
            }
            File mod = new File("./database/mod.txt");
            mod.createNewFile();

            FileReader fr = new FileReader(f);
            BufferedReader br = new BufferedReader(fr);

            FileWriter fw = new FileWriter(mod);
            PrintWriter pr = new PrintWriter(fw);

            String linea;
            String linea2 = Morse(Rotk(user)) + "|" + Morse(Rotk(user1)) + "|";
            while ((linea = br.readLine()) != null) {
                pr.println(linea);
                String[] fields = linea.split("\\|");
                if ((fields[0].equals(user1) || fields[1].equals(user1)) && (fields[0].equals(user) || fields[1].equals(user))) {
                    sw = 1;
                }
            }
            if (sw == 0) {
                pr.println(linea2);
            }

            pr.close();
            fw.close();
            br.close();
            fr.close();

            /*
            boolean delete = f.delete();
            boolean rename = mod.renameTo(f);
            System.out.println(delete + " " + rename);
             */
            boolean delete;
            boolean rename;
            do {
                delete = f.delete();
                rename = mod.renameTo(f);
                //System.out.println(delete + " " + rename);
            } while (!(delete && rename));
            if (sw == 0) {
                return true;
            }
        } catch (Exception ex) {
            System.out.println("Error en base de datos");
        }
        return false;
    }

    private boolean eraseToFriendFile(String user, String user1, boolean request) {
        boolean success = false;
        try {

            setModo(2); //drecrypt

            File f;
            int sw = 0;
            if (request) {
                f = new File(routeFriendRequests);
            } else {
                f = new File(routeFriends);
            }
            File mod = new File("./database/mod.txt");
            mod.createNewFile();

            FileReader fr = new FileReader(f);
            BufferedReader br = new BufferedReader(fr);

            FileWriter fw = new FileWriter(mod);
            PrintWriter pr = new PrintWriter(fw);

            String linea;
            while ((linea = br.readLine()) != null) {
                String[] fields = linea.split("\\|");
                for (int i = 0; i < fields.length; i++) {
                    fields[i] = Rotk(Morse(fields[i]));
                }
                if (!((fields[0].equalsIgnoreCase(user1) || fields[1].equalsIgnoreCase(user1)) && (fields[0].equalsIgnoreCase(user) || fields[1].equalsIgnoreCase(user)))) {
                    pr.println(linea);
                } else {
                    sw = 1;
                }
            }
            if (sw == 1) {
                success = true;
            }

            pr.close();
            fw.close();
            br.close();
            fr.close();

            boolean delete;
            boolean rename;
            do {
                delete = f.delete();
                rename = mod.renameTo(f);
                //System.out.println(delete + " " + rename);
            } while (!(delete && rename));
        } catch (Exception ex) {
            System.out.println("Error en base de datos");
        }
        return success;
    }

    //message methods
    public synchronized String getUserMessages(String user) {
        String chatMessages = "ChatMessages|";
        try {
            File f = new File(routeMessages);

            FileReader fr = new FileReader(f);
            BufferedReader br = new BufferedReader(fr);

            String linea;
            while ((linea = br.readLine()) != null) {
                String[] fields = linea.split("\\|");
                if (fields[0].equalsIgnoreCase(user) || fields[1].equalsIgnoreCase(user)) {
                    chatMessages = chatMessages.concat(fields[0] + "-" + fields[1] + "-" + fields[2] + "|");
                }
            }

            br.close();
            fr.close();
        } catch (Exception ex) {
            System.out.println("Error en base de datos");
        }
        if (chatMessages.equals("ChatMessages|")) {
            chatMessages = "NONE";
        }
        return chatMessages;
    }

    public synchronized boolean addToMessageFile(String user, String user1, String message) { //Should be public?
        try {
            File f = new File(routeMessages);

            File mod = new File("./database/mod.txt");
            mod.createNewFile();

            FileReader fr = new FileReader(f);
            BufferedReader br = new BufferedReader(fr);

            FileWriter fw = new FileWriter(mod);
            PrintWriter pr = new PrintWriter(fw);

            String linea;
            String linea2 = user + "|" + user1 + "|" + message + "|";
            while ((linea = br.readLine()) != null) {
                pr.println(linea);
            }
            pr.println(linea2);

            pr.close();
            fw.close();
            br.close();
            fr.close();

            boolean delete;
            boolean rename;
            do {
                delete = f.delete();
                rename = mod.renameTo(f);
                //System.out.println(delete + " " + rename);
            } while (!(delete && rename));
            return true;
        } catch (Exception ex) {
            System.out.println("Error en base de datos");
        }
        return false;
    }

    //control methods
    public String Rotk(String s) { //Sub-rutina para Encriptar/Desencriptar segun el modo 
        String s2 = "";
        int K = 200;
        //int Modo = 2;
        int H = 36;
        String ABCD = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";

        int n = s.length(), j = 0, salir = 0, pos = 0; //variables necesarias
        String v, v2, ABCk = "";
        for (int i = 0; i < H; i++) {
            pos = (i + K) % H;
            ABCk = ABCk + ABCD.substring(pos, pos + 1);
        }

        for (int i = 0; i < n; i++) {
            v = s.substring(i, i + 1).toUpperCase();
            if (v.equals(" ")) {
                s2 = s2 + " ";
            } else {
                j = 0;
                salir = 0;
                while ((j < H) && (salir == 0)) {
                    switch (Modo) {
                        case 1: //si es el modo 1 = Encriptar 
                            v2 = ABCD.substring(j, j + 1);
                            if (v.equals(v2)) {
                                s2 = s2 + ABCk.substring(j, j + 1);
                                salir = 1;
                            }
                            break;
                        case 2: //si es el modo 2 = Desencriptar 
                            v2 = ABCk.substring(j, j + 1);
                            if (v.equals(v2)) {
                                s2 = s2 + ABCD.substring(j, j + 1);
                                salir = 1;
                            }
                            break;
                    }
                    j++;
                }
            }
        }
        return s2;
    }

    public String Morse(String s) {
        String s2 = "";
        String ABCD = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890", ABCm[] = {".-", "-...", "-.-.", "-..", ".", "..-.", "--.", "....", "..", ".---", "-.-", ".-..", "--", "-.", "---", ".--.", "--.-", ".-.", "...", "-", "..-", "...-", ".--", "-..-", "-.--", "--..", ".----", "..---", "...--", "....-", ".....", "-....", "--...", "---..", "----.", "-----"};
        //int Modo = 2;
        int H = 36;

        int n = s.length(), j = 0, salir = 0, Esp = 0, h2, l = 0; //variables a necesitar
        String v, v2, sim = "";
        for (int i = 0; i < n; i++) {
            v = s.substring(i, i + 1);
            switch (Modo) {
                case 1: //si el modo = 1 Encriptar 
                    v = v.toUpperCase();
                    if (v.equals(" ")) {
                        s2 = s2 + " ";
                    } else {
                        j = 0;
                        salir = 0;
                        while ((j < H) && (salir == 0)) {
                            v2 = ABCD.substring(j, j + 1);
                            if (v.equals(v2)) {
                                s2 = s2 + ABCm[j] + " ";
                                salir = 1;
                            }
                            j++;
                        }
                    }
                    break;
                case 2: //si el modo = 2 Desencriptar 
                    if (v.equals(" ")) {
                        Esp++;
                        if (Esp == 2) {
                            s2 = s2 + " ";
                            Esp = 0;
                            sim = "";
                        } else if (!sim.isEmpty()) {
                            h2 = sim.length();
                            salir = 0;
                            j = 0;
                            while ((j < H) && (salir == 0)) {
                                v2 = ABCm[j];
                                if (sim.equals(v2)) {
                                    salir = 1;
                                    sim = "";
                                    s2 = s2 + ABCD.substring(j, j + 1);
                                }
                                j++;
                            }
                            if (salir == 0) {
                                //Error
                            }
                        }
                    } else {
                        Esp = 0;
                        sim = sim + v;
                    }
                    break;
            }
        }
        if (!sim.isEmpty()) {
            h2 = sim.length();
            salir = 0;
            j = 0;
            while ((j < H) && (salir == 0)) {
                v2 = ABCm[j];
                if (sim.equals(v2)) {
                    salir = 1;
                    sim = "";
                    s2 = s2 + ABCD.substring(j, j + 1);
                }
                j++;
            }
            if (salir == 0) {
                //Error
            }
        }
        return s2;
    }

    public boolean validateWyN(String s) {
        String ABC = "ABCDEFGHIJKLMNOPQRSTUVWXYZ ";
        String numbers = "1234567890";
        boolean salir = false;
        if (!s.isEmpty()) {
            int n = s.length(), i = 0, characters = 0;
            String v;
            while (!salir && (i < n)) {
                v = s.substring(i, i + 1).toUpperCase();
                if (!(ABC.contains(v) || numbers.contains(v))) {
                    salir = true;
                }
                if (!v.equals(" ")) {
                    characters++;
                }
                i++;
            }
            if (characters == 0) {
                salir = true;
            }
        } else {
            salir = true;
        }
        return !salir;
    }

    public boolean validateMorse(String s) {
        int n = s.length(), i = 0;
        boolean salir = false;
        String v;
        while (!salir && i < n) {
            v = s.substring(i, i + 1).toUpperCase();
            if (!((v.equals(".")) || (v.equals("-")) || (v.equals(" ")))) {
                salir = true;
            }
            i++;
        }
        return !salir;
    }

    public boolean validateWord(String s) {
        String ABC = "ABCDEFGHIJKLMNOPQRSTUVWXYZ ";
        boolean salir = false;
        if (!s.isEmpty()) {
            int n = s.length(), i = 0, characters = 0;
            String v;
            while (!salir && (i < n)) {
                v = s.substring(i, i + 1).toUpperCase();
                if (!ABC.contains(v)) {
                    salir = true;
                }
                if (!v.equals(" ")) {
                    characters++;
                }
                i++;
            }
            if (characters == 0) {
                salir = true;
            }
        } else {
            salir = true;
        }
        return !salir;
    }

    public boolean validateNumber(String s) {
        String numbers = "1234567890";
        boolean salir = false;
        if (!s.isEmpty()) {
            int n = s.length(), i = 0, point = 0;
            String v;
            while (!salir && (i < n)) {
                v = s.substring(i, i + 1).toUpperCase();
                if (!numbers.contains(v)) {
                    if (v.equals(".")) {
                        point++;
                    } else {
                        salir = true;
                    }
                    if (point > 1) {
                        salir = true;
                    }
                }
                i++;
            }
        } else {
            salir = true;
        }
        return !salir;
    }

    public boolean validateLength(String s, int range) {
        if (!s.isEmpty()) {
            if (s.length() >= range) {
                return true;
            }
        }
        return false;
    }

    //GETTERS SETTERS
    public String getRouteUser() {
        return routeUser;
    }

    public synchronized void setRouteUser(String routeUser) {
        this.routeUser = routeUser;
    }

    public String getRouteFriends() {
        return routeFriends;
    }

    public synchronized void setRouteFriends(String routeFriends) {
        this.routeFriends = routeFriends;
    }

    public String getRouteFriendRequests() {
        return routeFriendRequests;
    }

    public synchronized void setRouteFriendRequests(String routeFriendRequests) {
        this.routeFriendRequests = routeFriendRequests;
    }

    public int getModo() {
        return Modo;
    }

    public synchronized void setModo(int Modo) {
        this.Modo = Modo;
    }

}
