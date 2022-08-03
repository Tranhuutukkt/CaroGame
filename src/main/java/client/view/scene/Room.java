/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package client.view.scene;
import client.Client;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 *
 * @author trantu4120
 */
public class Room extends javax.swing.JFrame {
    ImageIcon userIcon;
    ImageIcon opUserIcon;
    ImageIcon userIconLastMove;
    ImageIcon opUserIconLastMove;
    
    String user = Client.socketHandler.getUser();
    String opUser = Client.socketHandler.getOpUser();
    String roomID = Client.socketHandler.getRoom();
    String userSymbol;
    
    
    final int COLUMN = 16, ROW = 16;
    boolean turn = false; //false: wait, true: your turn
    boolean statusGame = false; //ban co dang trong mot van game?

    JButton btnOnBoard[][];
    JButton lastMove = null;

    /**
     * Creates new form Room
     */
    public Room() {
        initComponents();
        this.setLocationRelativeTo(null);
        this.setTitle("Caro Game - " + roomID);
        roomId.setText(roomID);
        username.setText(user); 
        
        txtChat.setEditable(false);
        
        // close window event
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                if (statusGame){
                    JOptionPane.showMessageDialog(Room.this, "You can't exit while playing a game!", "Unable", JOptionPane.INFORMATION_MESSAGE);
                }
                else {
                    if (JOptionPane.showConfirmDialog(Room.this,
                        "This action will let you leave this room!", "Are you sure?",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION)
                    {
                        Client.socketHandler.leaveRoom(user);
                    }
                }
                
            }
        });
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    }
    
    
    public void setOpUser(){
        this.opUser = Client.socketHandler.getOpUser();
    }
    
    public void setTurn(boolean turn){
        this.turn = turn;
        System.out.println("Turn: " + this.turn);
    }
    
    public void setStatusGame(boolean status){
        this.statusGame = status;
    }
    
    public void initBoard() {
        boardGamePane.removeAll();
        btnOnBoard = new JButton[COLUMN + 2][ROW + 2];

        for (int row = 0; row < ROW; row++) {
            for (int column = 0; column < COLUMN; column++) {
                btnOnBoard[column][row] = this.createBoardButton(row, column);
                boardGamePane.add(btnOnBoard[column][row]);
            }
        }
        boardGamePane.validate();
        //boardGamePane.setEnabled(turn);
    }
    
    public JButton createBoardButton(int row, int column) {
        
        JButton b = new JButton();
        b.setFocusPainted(false);
        b.setBackground(new Color(180, 180, 180));
        b.setActionCommand("");
        b.setEnabled(false);

        b.addActionListener((ActionEvent e) -> {
            clickOnBoard(row, column, user);
        });

        
        b.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (b.getActionCommand().equals("")) {
                    b.setIcon(userIcon);
                    }
                }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (b.getActionCommand().equals("")) {
                    b.setIcon(null);
                }
            }
        });

        return b;
    }
    
    public void clickOnBoard(int row, int column, String user) {
        Client.socketHandler.move(row, column, user);
        
    }
    
    public void setAllBoardButtonEnabled(boolean x){
        for (int row = 0; row < ROW; row++) {
            for (int column = 0; column < COLUMN; column++) {
                if (btnOnBoard[column][row].getIcon() != null)
                    btnOnBoard[column][row].setEnabled(false);
                else btnOnBoard[column][row].setEnabled(x);
            }
        }
    }
    
    // change turn
    public void setTurnAfterMove() {
        if (!turn) {
            //turnTimer.restart();
            turn = !turn;
//            lbActive1.setVisible(true);
//            lbActive2.setVisible(false);
            username.setBorder(javax.swing.BorderFactory.createTitledBorder("Your turn..."));
            opUsername.setBorder(javax.swing.BorderFactory.createTitledBorder("Waiting..."));
        }
        else {
            //turnTimer.restart();
            turn = !turn;
//            lbActive1.setVisible(false);
//            lbActive2.setVisible(true);
            username.setBorder(javax.swing.BorderFactory.createTitledBorder("Waiting..."));
            opUsername.setBorder(javax.swing.BorderFactory.createTitledBorder("Your turn..."));
        }
    }
    
    public void startGame(){
        this.setStatusGame(true);
        btnStartNewGame.setVisible(false);
        //board
        boardGamePane.setLayout(new GridLayout(ROW, COLUMN));
        this.initBoard();
        this.setAllBoardButtonEnabled(turn);
    }
    
    public void addPoint(int row, int column, String user) {
        if (lastMove != null) {
            lastMove.setIcon(user.equals(this.user) ? opUserIcon : userIcon);
            lastMove.setDisabledIcon(user.equals(this.user) ? opUserIcon : userIcon);
        }

        lastMove = btnOnBoard[column][row];
        lastMove.setActionCommand(user); // save user as state

        if (user.equals(this.user)) {
            lastMove.setIcon(userIconLastMove);
            lastMove.setDisabledIcon(userIconLastMove);
        } else {
            lastMove.setIcon(opUserIconLastMove);
            lastMove.setDisabledIcon(opUserIconLastMove);
        }
        this.setTurnAfterMove();
        this.setAllBoardButtonEnabled(turn);
    }
    
    public void setWin(String[] data){
        String dataUser = data[1];
        if (!dataUser.equals(this.user)) {
            if (dataUser.equals(this.opUser)){
                JOptionPane.showMessageDialog(this, "YOU LOSE.", "Lose", JOptionPane.INFORMATION_MESSAGE);
            }
                JOptionPane.showMessageDialog(this, "YOU LOSE.", "Lose", JOptionPane.INFORMATION_MESSAGE);
        } else JOptionPane.showMessageDialog(this, "YOU WIN!", "Win", JOptionPane.INFORMATION_MESSAGE);
        
        for (int i = 2; i <= 6; i++){
            int cell = Integer.parseInt(data[i]);
            int col = cell % COLUMN;
            int row = cell / COLUMN;
            this.btnOnBoard[col][row].setDisabledIcon(dataUser.equals(this.user) ? userIconLastMove : opUserIconLastMove);
        }
        btnStartNewGame.setVisible(true);
        this.setAllBoardButtonEnabled(false);
        this.setStatusGame(false);
    }
    
    public void joinRoom(){
        this.setOpUser();
        opUsername.setText(opUser);
        userSymbol = Client.socketHandler.getSymbol();
        
        btnStartNewGame.setEnabled((opUser != null));
        if (userSymbol != null){
            if (userSymbol.equals("x")){
                userIcon = new ImageIcon("src/main/java/img/cancel.png");
                opUserIcon = new ImageIcon("src/main/java/img/circle.png");
                userIconLastMove = new ImageIcon("src/main/java/img/cancel-lastmove.png");
                opUserIconLastMove = new ImageIcon("src/main/java/img/circle-lastmove.png");
            }
            else {
                opUserIcon = new ImageIcon("src/main/java/img/cancel.png");
                userIcon = new ImageIcon("src/main/java/img/circle.png");
                opUserIconLastMove = new ImageIcon("src/main/java/img/cancel-lastmove.png");
                userIconLastMove = new ImageIcon("src/main/java/img/circle-lastmove.png");
            }
        }
        
        lbUserSymbol.setIcon(userIcon);
        lbOpUserSymbol.setIcon(opUserIcon);
    }
    
    public void leaveRoom(String dataUser){
        if (this.user.equals(dataUser)){
            Client.closeScene(Client.SceneName.ROOM);
            Client.openScene(Client.SceneName.MENU);
            JOptionPane.showMessageDialog(Client.menuScene, "You have left the room successfully!", "OK", JOptionPane.INFORMATION_MESSAGE);
        }
        else {
            this.setOpUser();
            opUsername.setText(opUser);
            btnStartNewGame.setEnabled(false);
            JOptionPane.showMessageDialog(Client.menuScene, dataUser + " have left the room successfully!", "OK", JOptionPane.INFORMATION_MESSAGE);
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
        roomId = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        username = new javax.swing.JLabel();
        opUsername = new javax.swing.JLabel();
        boardGamePane = new javax.swing.JPanel();
        chatPane = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtChat = new javax.swing.JTextPane();
        txChatMessage = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        btnEndRoom = new javax.swing.JButton();
        lbUserSymbol = new javax.swing.JLabel();
        lbOpUserSymbol = new javax.swing.JLabel();
        btnStartNewGame = new javax.swing.JButton();
        timeLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setText("Room ID:");

        roomId.setText("roomId");

        jLabel2.setText("You:");

        jLabel3.setText("Your opponent:");

        username.setText("Your username");

        opUsername.setText("Your opponent");

        boardGamePane.setLayout(new java.awt.GridLayout(1, 0));

        jScrollPane2.setViewportView(txtChat);

        jButton1.setFont(new java.awt.Font("Liberation Sans", 0, 12)); // NOI18N
        jButton1.setText("SEND");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout chatPaneLayout = new javax.swing.GroupLayout(chatPane);
        chatPane.setLayout(chatPaneLayout);
        chatPaneLayout.setHorizontalGroup(
            chatPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(chatPaneLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(chatPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(chatPaneLayout.createSequentialGroup()
                        .addComponent(jScrollPane2)
                        .addContainerGap())
                    .addGroup(chatPaneLayout.createSequentialGroup()
                        .addComponent(txChatMessage, javax.swing.GroupLayout.PREFERRED_SIZE, 242, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 61, Short.MAX_VALUE)
                        .addGap(12, 12, 12))))
        );
        chatPaneLayout.setVerticalGroup(
            chatPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(chatPaneLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 484, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(chatPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txChatMessage, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(19, 19, 19))
        );

        btnEndRoom.setText("Leave Room");
        btnEndRoom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEndRoomActionPerformed(evt);
            }
        });

        btnStartNewGame.setText("Start new game");
        btnStartNewGame.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnStartNewGameActionPerformed(evt);
            }
        });

        timeLabel.setFont(new java.awt.Font("Liberation Sans", 1, 36)); // NOI18N
        timeLabel.setText("Time");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(25, 25, 25)
                        .addComponent(boardGamePane, javax.swing.GroupLayout.PREFERRED_SIZE, 700, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(38, 38, 38)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(chatPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(btnStartNewGame)
                                .addGap(26, 26, 26)
                                .addComponent(timeLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap(98, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(roomId, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnEndRoom)
                        .addGap(35, 35, 35))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(username, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(lbUserSymbol, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(349, 349, 349)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lbOpUserSymbol, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(opUsername, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(roomId, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnEndRoom)
                            .addGroup(layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lbUserSymbol, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lbOpUserSymbol, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel2)
                                    .addComponent(jLabel3)
                                    .addComponent(username, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(opUsername, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                        .addGap(34, 34, 34)
                        .addComponent(boardGamePane, javax.swing.GroupLayout.DEFAULT_SIZE, 710, Short.MAX_VALUE)
                        .addContainerGap(11, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnStartNewGame, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(timeLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(chatPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnEndRoomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEndRoomActionPerformed
    
    }//GEN-LAST:event_btnEndRoomActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton1ActionPerformed

    private void btnStartNewGameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStartNewGameActionPerformed
        Client.socketHandler.startGame(user);
    }//GEN-LAST:event_btnStartNewGameActionPerformed

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
            java.util.logging.Logger.getLogger(Room.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Room.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Room.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Room.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Room().setVisible(true);
            };
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel boardGamePane;
    private javax.swing.JButton btnEndRoom;
    private javax.swing.JButton btnStartNewGame;
    private javax.swing.JPanel chatPane;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lbOpUserSymbol;
    private javax.swing.JLabel lbUserSymbol;
    private javax.swing.JLabel opUsername;
    private javax.swing.JLabel roomId;
    private javax.swing.JLabel timeLabel;
    private javax.swing.JTextField txChatMessage;
    private javax.swing.JTextPane txtChat;
    private javax.swing.JLabel username;
    // End of variables declaration//GEN-END:variables
}
