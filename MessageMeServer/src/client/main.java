/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author john
 */
public class main {

    public static void main(String[] args) {
        try {
            Client tester = new Client();

            BufferedReader read = new BufferedReader(new InputStreamReader(System.in));
            int op;

            System.out.println("Press 1 to finish connection.");
            do {
                op = Integer.valueOf(read.readLine());

            } while (op != 1);
        } catch (Exception e) {
            System.out.println("Connection lost.");
        }

    }
}
