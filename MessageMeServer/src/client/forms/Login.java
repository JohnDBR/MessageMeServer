/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.forms;

import client.Client;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 *
 * @author john
 */
public class Login extends javax.swing.JFrame {

    private Client client = null;

    /**
     * Creates new form LogIn
     */
    public Login() {
        initComponents();

        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent event) {
                exitProcedure();
            }
        });
        this.setVisible(true);

        initConnection();
    }

    public void exitProcedure() {
        client.close();
        this.dispose();
        System.exit(0);
    }

    private void initConnection() {
        client = new Client();
    }

    private void authenticateRequest(String user, char[] dots) {
        String password = "";
        for (char dot : dots) {
            password = password + dot;
        }
        String message = "Login-" + user + "-" + password + "-";
        try {
            client.sendMessage(message);
        } catch (IOException ex) {
            System.out.println("Problem sending the message");
        }
        String answer = "";
        do {
            try {
                answer = client.receiveMessage();
            } catch (IOException ex) {
                System.out.println("Problem receiving messages");
            }
        } while (answer.equals(""));
        if (answer.equals("Login Successfully")) {
            String userFriends = "";
            do {
                try {
                    userFriends = client.receiveMessage();
                } catch (IOException ex) {
                    System.out.println("Problem receiving messages");
                }
            } while (userFriends.equals(""));
            String chatMessages = "";
            do {
                try {
                    chatMessages = client.receiveMessage();
                } catch (IOException ex) {
                    System.out.println("Problem receiving messages");
                }
            } while (chatMessages.equals(""));
            client.setUser(user);
            new ChatForm(client, userFriends, chatMessages);
            this.dispose();
        } else if (answer.equals("Login Unsuccessfully")) {
            JOptionPane.showMessageDialog(null, "Username o Password erroneos, por favor verifique");
        } else {
            JOptionPane.showMessageDialog(null, "Sesion iniciada en otro computador");
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        tUser = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        tPassword = new javax.swing.JPasswordField();
        bLogIn = new javax.swing.JButton();
        bRegistration = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Login");
        setResizable(false);

        jLabel1.setText("              Usuario:");

        jLabel2.setText("             Contraseña:");

        bLogIn.setText("Log in");
        bLogIn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bLogInActionPerformed(evt);
            }
        });

        bRegistration.setText("Sign up");
        bRegistration.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bRegistrationActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(100, 100, 100)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(bLogIn)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(bRegistration))
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(tUser)
                    .addComponent(tPassword, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(100, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tUser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(bLogIn)
                    .addComponent(bRegistration))
                .addContainerGap(76, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void bLogInActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bLogInActionPerformed
        authenticateRequest(tUser.getText(), tPassword.getPassword());

    }//GEN-LAST:event_bLogInActionPerformed

    private void bRegistrationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bRegistrationActionPerformed
        new UserCreation(client);
        this.dispose();
    }//GEN-LAST:event_bRegistrationActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            Login Start = new Login();
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bLogIn;
    private javax.swing.JButton bRegistration;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPasswordField tPassword;
    private javax.swing.JTextField tUser;
    // End of variables declaration//GEN-END:variables
}
