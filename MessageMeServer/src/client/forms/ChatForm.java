/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.forms;

import client.Client;
import java.awt.Component;
import java.awt.Container;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author john
 */
public class ChatForm extends javax.swing.JFrame {

    Client client = null;

    /**
     * Creates new form ClientFrame
     */
    public ChatForm(Client client, String userFriends) {
        initComponents();
        this.setLocationRelativeTo(null);
        this.setVisible(true);

        this.client = client;
        init();
        loadUserFriends(userFriends);
    }

    private void init() {
        lUser.setText(client.getUser() + "'s Profile");
        initThreads();

        table();
        //enableComponents(pChat, false);
        //enableComponents(pFriend, true);
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

    private void table() {
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
                        //Later....!!!!! (Charge the conversation da...!)
                    }
                }

            }
        });
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
        Thread incomingMessages = new Thread(() -> {
            String s;
            while (true) {
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
                    taMessages.setText(taMessages.getText() + "\n" + fields[1] + "-" + fields[3]);
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
                        .addGroup(pChatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 362, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lFriend, javax.swing.GroupLayout.PREFERRED_SIZE, 178, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 13, Short.MAX_VALUE)))
                .addContainerGap())
        );
        pChatLayout.setVerticalGroup(
            pChatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pChatLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lFriend)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 202, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pChatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(bSend)
                    .addComponent(tMessage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(lUser)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(pFriend, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pChat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(lUser)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(pFriend, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(pChat, javax.swing.GroupLayout.DEFAULT_SIZE, 307, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void bSendActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bSendActionPerformed

        if (client != null && !tMessage.getText().equals("")) {
            try {
                //String message = "ChatMessage-" + client.getUser() + "-" + tMessage.getText();
                String message = tMessage.getText();
                client.sendMessage(message);
                taMessages.setText(taMessages.getText() + "\n" + tMessage.getText());
                tMessage.setText("");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "Error al enviar!");
            }
        }

    }//GEN-LAST:event_bSendActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable TFriendRequests;
    private javax.swing.JTable TFriends;
    private javax.swing.JButton bSend;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel lFriend;
    private javax.swing.JLabel lUser;
    private javax.swing.JPanel pChat;
    private javax.swing.JPanel pFriend;
    private javax.swing.JTextField tMessage;
    public static javax.swing.JTextArea taMessages;
    // End of variables declaration//GEN-END:variables
}
