/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.forms;

import client.Client;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author john
 */
public class ChatForm extends javax.swing.JFrame {

    private Client client = null;

    private String selectedFriend = "";
    private LinkedList<String> chatMessages;

    private boolean isStopped = false;
    Thread incomingMessages;

    /**
     * Creates new form ClientFrame
     */
    public ChatForm(Client client, String userFriends, String chatMessages) {
        initComponents();
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent event) {
                exitProcedure();
            }
        });
        this.setVisible(true);

        this.client = client;

        init();
        loadUserFriends(userFriends);
        loadUserChatMessages(chatMessages);
    }

    public void exitProcedure() {
        isStopped = true;
        try {
            //client.close(); //for some reason socket is rebellious...
            client.sendMessage("Close"); //am i to tough?...
        } catch (IOException ex) {
            Logger.getLogger(ChatForm.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.dispose();
        System.exit(0);
    }

    private void init() {
        lUser.setText(client.getUser() + "'s Profile");
        initThreads();

        listeners();
        enableComponents(pChat, false);
        enableComponents(pFriend, true);
    }

    private void loadUserFriends(String userFriends) {
        if (!userFriends.equals("NONE")) {
            String[] friends = userFriends.split("\\|");
            for (int i = 1; i < friends.length; i++) {
                String[] fields = friends[i].split("\\-");
                if (Boolean.valueOf(fields[1])) {
                    addToTable(fields[0], false);
                } else {
                    addToTable(fields[0], true);
                }
            }
        }
    }

    private void loadUserChatMessages(String chatMessages) {
        if (!chatMessages.equals("NONE")) {
            System.out.println(chatMessages);

            String[] messages = chatMessages.split("\\|");
            this.chatMessages = new LinkedList<>();
            for (int i = 1; i < messages.length; i++) {
                this.chatMessages.add(messages[i]);
            }
            //for (String message : messages) { //dont save chatMessages title in the array da...!!
            //    this.chatMessages.add(message);
            //}
        }

    }

    private void listeners() {
        TFriendRequests.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent event) {
                if (event.getValueIsAdjusting()) {
                    int selectedRowIndex = TFriendRequests.getSelectedRow();
                    int selectedColumnIndex = 0;
                    String user;
                    try {
                        user = TFriendRequests.getModel().getValueAt(selectedRowIndex, selectedColumnIndex).toString(); //FIXX!!!
                    } catch (Exception ex) {
                        user = "";
                    }
                    if (!user.isEmpty()) {
                        deleteToTable(user, true);
                        if (JOptionPane.showConfirmDialog(null, user + " want to be your friend!", "WARNING", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                            try {
                                client.sendMessage("FriendRequest-" + client.getUser().toUpperCase() + "-" + user.toUpperCase() + "-" + "true");//Send true request
                            } catch (IOException ex) {
                                System.out.println("Problem send the request!");
                            }
                        } else {
                            try {
                                client.sendMessage("FriendRequest-" + client.getUser().toUpperCase() + "-" + user.toUpperCase() + "-" + "false");//Send false request
                            } catch (IOException ex) {
                                System.out.println("Problem send the request!");
                            }
                        }
                    }
                }

            }
        });

        TFriends.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent event) {
                if (event.getValueIsAdjusting()) {
                    int selectedRowIndex = TFriends.getSelectedRow();
                    int selectedColumnIndex = 0;
                    String user;
                    try {
                        user = TFriends.getModel().getValueAt(selectedRowIndex, selectedColumnIndex).toString(); //FIXX!!!
                    } catch (Exception ex) {
                        user = "";
                    }
                    if (!user.isEmpty()) {
                        if (selectedFriend.equals("")) {
                            enableComponents(pChat, true);
                        }
                        selectedFriend = user;
                        lFriend.setText("User - " + user);
                        loadConversation(user);
                    }
                }

            }
        });

        tMessage.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (c == '|' || c == '-') {
                    e.consume();  // ignore event
                }
            }
        });

        tFriendUser.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (c == '|' || c == '-') {
                    e.consume();  // ignore event
                }
            }
        });
    }

    private void loadConversation(String user) {
        taMessages.setText("");
        for (String chatMessage : chatMessages) {
            String[] fields = chatMessage.split("\\-");
            if (fields[0].equalsIgnoreCase(user) || fields[1].equalsIgnoreCase(user)) {
                taMessages.setText(taMessages.getText() + "\n" + fields[0] + "-" + fields[2]);
            }
        }
    }

    private void enableComponents(Container container, boolean enable) {
        Component[] components = container.getComponents();
        for (Component component : components) {
            component.setEnabled(enable);
            if (component instanceof Container) {
                enableComponents((Container) component, enable);
            }
        }
    }

    private void initThreads() {
        incomingMessages = new Thread(() -> {
            String s;
            while (!isStopped) {
                if (client != null) {
                    try {
                        s = client.receiveMessage();
                        messageProcessor(s);
                    } catch (Exception e) {
                        System.out.println("I dont know!");
                    };
                }
            }
        });
        incomingMessages.start();
    }

    private void messageProcessor(String message) {
        String[] fields = message.split("-");
        int result;
        if (fields.length == 1) {
            JOptionPane.showMessageDialog(null, message);
        } else {
            switch (fields[0]) {
                case "ChatMessage":
                    chatMessages.add(fields[1] + "-" + fields[2] + "-" + fields[3]);
                    if (selectedFriend.equalsIgnoreCase(fields[1])) {
                        taMessages.setText(taMessages.getText() + "\n" + fields[1] + "-" + fields[3]);
                    } else {
                        //Notification idea!...
                    }
                    break;
                case "FriendRequest":
                    addToTable(fields[1], true);
                    break;
                case "NewFriend":
                    addToTable(fields[1], false);
                    break;
                case "DeleteFriend":
                    deleteToTable(fields[1], false);
                    break;
            }
        }
    }

    private void addToTable(String string, boolean request) {
        DefaultTableModel model;
        if (request) {
            model = (DefaultTableModel) this.TFriendRequests.getModel();
        } else {
            model = (DefaultTableModel) this.TFriends.getModel();
        }
        model.addRow(new Object[]{string});
    }

    private void deleteToTable(String string, boolean request) {
        DefaultTableModel model;
        int rowCount;
        if (request) {
            model = (DefaultTableModel) this.TFriendRequests.getModel();
            rowCount = TFriendRequests.getRowCount();
        } else {
            model = (DefaultTableModel) this.TFriends.getModel();
            rowCount = TFriends.getRowCount();
        }
        for (int i = 0; i < rowCount; i++) {
            if (model.getValueAt(i, 0).equals(string)) {
                model.removeRow(i);
            }
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

        pFriend = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        TFriendRequests = new javax.swing.JTable();
        jScrollPane3 = new javax.swing.JScrollPane();
        TFriends = new javax.swing.JTable();
        lUser = new javax.swing.JLabel();
        pChat = new javax.swing.JPanel();
        tMessage = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        taMessages = new javax.swing.JTextArea();
        bSend = new javax.swing.JButton();
        lFriend = new javax.swing.JLabel();
        bDeleteFriend = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        tFriendUser = new javax.swing.JTextField();
        bAddFriend = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("MESSAGE-ME");
        setMinimumSize(new java.awt.Dimension(400, 374));
        setResizable(false);

        TFriendRequests.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Friend Requests"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(TFriendRequests);
        if (TFriendRequests.getColumnModel().getColumnCount() > 0) {
            TFriendRequests.getColumnModel().getColumn(0).setResizable(false);
        }

        TFriends.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Friends"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane3.setViewportView(TFriends);
        if (TFriends.getColumnModel().getColumnCount() > 0) {
            TFriends.getColumnModel().getColumn(0).setResizable(false);
        }

        javax.swing.GroupLayout pFriendLayout = new javax.swing.GroupLayout(pFriend);
        pFriend.setLayout(pFriendLayout);
        pFriendLayout.setHorizontalGroup(
            pFriendLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pFriendLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pFriendLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 178, Short.MAX_VALUE)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pFriendLayout.setVerticalGroup(
            pFriendLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pFriendLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(16, Short.MAX_VALUE))
        );

        lUser.setText("NULL's Profile");

        pChat.setPreferredSize(new java.awt.Dimension(400, 374));

        taMessages.setEditable(false);
        taMessages.setColumns(20);
        taMessages.setRows(5);
        jScrollPane1.setViewportView(taMessages);

        bSend.setText("Enviar");
        bSend.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bSendActionPerformed(evt);
            }
        });

        lFriend.setText("User - ");

        bDeleteFriend.setText("Eliminar Amigo");
        bDeleteFriend.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bDeleteFriendActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pChatLayout = new javax.swing.GroupLayout(pChat);
        pChat.setLayout(pChatLayout);
        pChatLayout.setHorizontalGroup(
            pChatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pChatLayout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(pChatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pChatLayout.createSequentialGroup()
                        .addComponent(tMessage)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(bSend))
                    .addGroup(pChatLayout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 362, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 13, Short.MAX_VALUE))
                    .addGroup(pChatLayout.createSequentialGroup()
                        .addComponent(lFriend, javax.swing.GroupLayout.PREFERRED_SIZE, 178, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(bDeleteFriend)))
                .addContainerGap())
        );
        pChatLayout.setVerticalGroup(
            pChatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pChatLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(pChatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lFriend)
                    .addComponent(bDeleteFriend))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 202, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pChatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(bSend)
                    .addComponent(tMessage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18))
        );

        jLabel1.setText("Usuario: ");

        bAddFriend.setText("Agregar Amigo");
        bAddFriend.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bAddFriendActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addComponent(pFriend, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(24, 24, 24)
                        .addComponent(lUser)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pChat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(tFriendUser, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(bAddFriend)
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lUser)
                    .addComponent(jLabel1)
                    .addComponent(tFriendUser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(bAddFriend))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(pFriend, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(pChat, javax.swing.GroupLayout.DEFAULT_SIZE, 314, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void bSendActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bSendActionPerformed

        if (client != null && !tMessage.getText().equals("") && !selectedFriend.equals("")) {
            try {
                String message = client.getUser().toUpperCase() + "-" + selectedFriend.toUpperCase() + "-" + tMessage.getText();
                String request = "ChatMessage-" + message;
                //String message = tMessage.getText();
                client.sendMessage(request);
                chatMessages.add(message);
                taMessages.setText(taMessages.getText() + "\n" + client.getUser().toUpperCase() + "-" + tMessage.getText());
                tMessage.setText("");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "Error al enviar!");
            }
        }

    }//GEN-LAST:event_bSendActionPerformed

    private void bAddFriendActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bAddFriendActionPerformed

        if (client != null && !tFriendUser.getText().equals("")) {
            try {
                String message = "FriendRequest-" + client.getUser() + "-" + tFriendUser.getText() + "-true";
                client.sendMessage(message);
                tFriendUser.setText("");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Error al enviar!");
            }
        }
    }//GEN-LAST:event_bAddFriendActionPerformed

    private void bDeleteFriendActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bDeleteFriendActionPerformed

        if (client != null && !selectedFriend.equals("")) {
            try {
                String message = "FriendRequest-" + client.getUser() + "-" + selectedFriend + "-false";
                client.sendMessage(message);
                tFriendUser.setText("");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Error al enviar!");
            }
        }
    }//GEN-LAST:event_bDeleteFriendActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable TFriendRequests;
    private javax.swing.JTable TFriends;
    private javax.swing.JButton bAddFriend;
    private javax.swing.JButton bDeleteFriend;
    private javax.swing.JButton bSend;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel lFriend;
    private javax.swing.JLabel lUser;
    private javax.swing.JPanel pChat;
    private javax.swing.JPanel pFriend;
    private javax.swing.JTextField tFriendUser;
    private javax.swing.JTextField tMessage;
    public static javax.swing.JTextArea taMessages;
    // End of variables declaration//GEN-END:variables
}
