/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package client.view.scene;
import client.Client;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.concurrent.Callable;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

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
    
    StyledDocument doc;
    SimpleAttributeSet center = new SimpleAttributeSet();
    SimpleAttributeSet left = new SimpleAttributeSet();
    SimpleAttributeSet right = new SimpleAttributeSet();
    
    final int COLUMN = 16, ROW = 16;
    boolean turn = false; //false: wait, true: your turn
    boolean statusGame = false; //ban co dang trong mot van game?

    JButton btnOnBoard[][];
    JButton lastMove = null;
    int moveNumber;
    
    CountDownTimer turnTimer;

    /**
     * Creates new form Room
     */
    public Room() {
        initComponents();
        this.setLocationRelativeTo(null);
        this.setTitle("Caro Game - " + roomID);
        roomId.setText(roomID);
        username.setText(user);
        
        btnStartNewGame.setEnabled(false);
        
        txtChat.setEditable(false);
        DefaultCaret caret = (DefaultCaret)txtChat.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        
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
    public void setMoveNumber(int num){
        this.moveNumber = num;
        lbMoveNumber.setText(Integer.toString(moveNumber));
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
        this.setMoveNumber(ROW*COLUMN);
        turnTimer = new CountDownTimer(30);
        turnTimer.setTimerCallBack(
            // end match callback
            null,
            // tick match callback
            (Callable) new Callable() {
            @Override
            public Object call() throws Exception {
                timeLabel.setText(Integer.toString(turnTimer.getCurrentTick()));
                return null;
            }
        },
            // tick interval
            1
        );
        
        if (timeLabel.getText().equals(0)) {
            if (turn != true) {
                JOptionPane.showMessageDialog(Room.this, "YOU WIN!", "Win", JOptionPane.INFORMATION_MESSAGE);
                Client.socketHandler.setPoint(Room.this.user, Room.this.roomID, 1);
            }
            else
            {
                JOptionPane.showMessageDialog(Room.this, "YOU LOSE.", "Lose", JOptionPane.INFORMATION_MESSAGE);
                Client.socketHandler.setPoint(Room.this.user, Room.this.roomID, -1);
            }
        }
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
        this.setMoveNumber(--this.moveNumber);
        if (this.moveNumber == 0){
            JOptionPane.showMessageDialog(this, "DRAW", "No one win.", JOptionPane.INFORMATION_MESSAGE);
            Client.socketHandler.setPoint(this.user, this.roomID, 0);
        }
        turnTimer = new CountDownTimer(30);
        turnTimer.setTimerCallBack(
                // end match callback
                null,
                // tick match callback
                (Callable) () -> {
                    timeLabel.setText(Integer.toString(turnTimer.getCurrentTick()));
                    return null;
                },
                // tick interval
                1
        );
    }
    
    public void setWin(String[] data){
        String dataUser = data[1];
        
        for (int i = 2; i <= 6; i++){
            int cell = Integer.parseInt(data[i]);
            int col = cell % COLUMN;
            int row = cell / COLUMN;
            this.btnOnBoard[col][row].setDisabledIcon(dataUser.equals(this.user) ? userIconLastMove : opUserIconLastMove);
        }
        
        if (!dataUser.equals(this.user)) {
            if (dataUser.equals(this.opUser)){
                JOptionPane.showMessageDialog(this, "YOU LOSE.", "Lose", JOptionPane.INFORMATION_MESSAGE);
                Client.socketHandler.setPoint(this.user, this.roomID, -1);
            }
        } else {
            JOptionPane.showMessageDialog(this, "YOU WIN!", "Win", JOptionPane.INFORMATION_MESSAGE);
            Client.socketHandler.setPoint(this.user, this.roomID, 1);
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
            JOptionPane.showMessageDialog(Client.menuScene, dataUser + " have left the room!", "OK", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    public void addChat(String[] data){
        String dataUser = data[1];
        
        doc = txtChat.getStyledDocument();
        String[] message = {"", ""};
        message[0] = data[2] + " " + (dataUser.equals(this.user) ? "You:" : (dataUser + ":"));
        message[1] = data[3];
        //  Define the attribute you want for the line of text
        StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
        StyleConstants.setAlignment(left, StyleConstants.ALIGN_LEFT);
        StyleConstants.setAlignment(right, StyleConstants.ALIGN_RIGHT);
        for (int i = 0; i< 2; i++){
            try
            {
                int length = doc.getLength();
                doc.insertString(doc.getLength(), "\n" + message[i], null);
                if (dataUser.equals(this.user))
                    doc.setParagraphAttributes(length+1, 1, right, false);
                else if (dataUser.equals("SYSTEM"))
                    doc.setParagraphAttributes(length+1, 1, center, false);
                else
                    doc.setParagraphAttributes(length+1, 1, left, false);
            }
            catch(BadLocationException e) { System.out.println(e);}     
        }
        
    }
    
    public void setPoint(String[] data){
        String[] message = {"", "SYSTEM", "", ""};
        message[2] = data[1];
        switch (Integer.parseInt(data[2])) {
            case 1:
                message[3] = "You won - Your score: ";
                break;
            case -1:
                message[3] = "You lose - Your score: ";
                break;
            case 0:
                message[3] = "No one win - Your score: ";
                break;
            default:
                break;
        }
        message[3] += data[3];
        this.addChat(message);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
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
        btnSend = new javax.swing.JButton();
        btnEndRoom = new javax.swing.JButton();
        lbUserSymbol = new javax.swing.JLabel();
        lbOpUserSymbol = new javax.swing.JLabel();
        btnStartNewGame = new javax.swing.JButton();
        timeLabel = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        lbMoveNumber = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(0, 204, 255));

        jLabel1.setFont(new java.awt.Font("URW Gothic", 1, 14)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(51, 51, 51));
        jLabel1.setText("Room ID:");

        roomId.setFont(new java.awt.Font("URW Gothic", 3, 18)); // NOI18N
        roomId.setForeground(new java.awt.Color(255, 51, 0));
        roomId.setText("roomId123");

        jLabel2.setFont(new java.awt.Font("URW Gothic", 1, 14)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(51, 51, 51));
        jLabel2.setText("You:");

        jLabel3.setFont(new java.awt.Font("URW Gothic", 1, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(51, 51, 51));
        jLabel3.setText("Your opponent:");

        username.setFont(new java.awt.Font("URW Gothic", 1, 14)); // NOI18N
        username.setForeground(new java.awt.Color(255, 51, 0));
        username.setText("Your username");

        opUsername.setFont(new java.awt.Font("URW Gothic", 1, 14)); // NOI18N
        opUsername.setForeground(new java.awt.Color(51, 51, 51));
        opUsername.setText("Your opponent");

        boardGamePane.setBackground(new java.awt.Color(255, 153, 255));
        boardGamePane.setLayout(new java.awt.GridLayout(1, 0));

        chatPane.setBackground(new java.awt.Color(255, 153, 255));
        chatPane.setForeground(new java.awt.Color(255, 255, 255));

        jScrollPane2.setAutoscrolls(true);

        txtChat.setFont(new java.awt.Font("VNI-Vari", 1, 14)); // NOI18N
        jScrollPane2.setViewportView(txtChat);

        txChatMessage.setFont(new java.awt.Font("VNI-Vari", 1, 14)); // NOI18N
        txChatMessage.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txChatMessageKeyPressed(evt);
            }
        });

        btnSend.setBackground(new java.awt.Color(255, 204, 0));
        btnSend.setFont(new java.awt.Font("URW Gothic", 1, 14)); // NOI18N
        btnSend.setForeground(new java.awt.Color(255, 255, 255));
        btnSend.setText("SEND");
        btnSend.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnSendMouseClicked(evt);
            }
        });
        btnSend.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSendActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout chatPaneLayout = new javax.swing.GroupLayout(chatPane);
        chatPane.setLayout(chatPaneLayout);
        chatPaneLayout.setHorizontalGroup(
            chatPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(chatPaneLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(chatPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2)
                    .addGroup(chatPaneLayout.createSequentialGroup()
                        .addComponent(txChatMessage, javax.swing.GroupLayout.PREFERRED_SIZE, 242, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnSend, javax.swing.GroupLayout.PREFERRED_SIZE, 67, Short.MAX_VALUE)))
                .addContainerGap())
        );
        chatPaneLayout.setVerticalGroup(
            chatPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(chatPaneLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 544, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(chatPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txChatMessage, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSend, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(19, 19, 19))
        );

        btnEndRoom.setBackground(new java.awt.Color(102, 102, 102));
        btnEndRoom.setFont(new java.awt.Font("URW Gothic", 1, 14)); // NOI18N
        btnEndRoom.setForeground(new java.awt.Color(255, 255, 255));
        btnEndRoom.setText("Leave Room");
        btnEndRoom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEndRoomActionPerformed(evt);
            }
        });

        lbUserSymbol.setFont(new java.awt.Font("VNI-Vari", 1, 14)); // NOI18N

        lbOpUserSymbol.setFont(new java.awt.Font("VNI-Vari", 1, 14)); // NOI18N

        btnStartNewGame.setBackground(new java.awt.Color(255, 204, 0));
        btnStartNewGame.setFont(new java.awt.Font("URW Gothic", 1, 18)); // NOI18N
        btnStartNewGame.setForeground(new java.awt.Color(255, 255, 255));
        btnStartNewGame.setText("Start new game");
        btnStartNewGame.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnStartNewGameActionPerformed(evt);
            }
        });

        timeLabel.setFont(new java.awt.Font("VNI-Vari", 1, 18)); // NOI18N
        timeLabel.setForeground(new java.awt.Color(255, 255, 255));
        timeLabel.setText("Time");

        jButton1.setFont(new java.awt.Font("URW Gothic", 1, 14)); // NOI18N
        jButton1.setText("Copy");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("URW Gothic", 1, 14)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(51, 51, 51));
        jLabel4.setText("Moves left:");

        lbMoveNumber.setFont(new java.awt.Font("URW Gothic", 3, 18)); // NOI18N
        lbMoveNumber.setForeground(new java.awt.Color(255, 51, 0));
        lbMoveNumber.setText("0");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(43, 43, 43)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addGap(43, 43, 43)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(username, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(lbUserSymbol, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(131, 131, 131)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(opUsername, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(lbOpUserSymbol, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(timeLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(145, 145, 145))))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addComponent(boardGamePane, javax.swing.GroupLayout.PREFERRED_SIZE, 700, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(38, 38, 38)
                                .addComponent(btnStartNewGame, javax.swing.GroupLayout.PREFERRED_SIZE, 207, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnEndRoom, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(47, 47, 47)
                                .addComponent(chatPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(roomId, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1)
                        .addGap(249, 249, 249)
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lbMoveNumber, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(0, 79, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(37, 37, 37)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel1)
                                    .addComponent(roomId, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(32, 32, 32)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel4)
                                        .addComponent(lbMoveNumber, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jButton1))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lbOpUserSymbol, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lbUserSymbol, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(timeLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(username, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(opUsername, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(25, 25, 25)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(boardGamePane, javax.swing.GroupLayout.PREFERRED_SIZE, 700, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnEndRoom, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnStartNewGame, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(31, 31, 31)
                        .addComponent(chatPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(23, 23, 23))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnEndRoomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEndRoomActionPerformed
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
    }//GEN-LAST:event_btnEndRoomActionPerformed

    private void btnSendActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSendActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnSendActionPerformed

    private void btnStartNewGameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStartNewGameActionPerformed
        Client.socketHandler.startGame(user);
    }//GEN-LAST:event_btnStartNewGameActionPerformed

    private void txChatMessageKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txChatMessageKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            btnSendMouseClicked(null);
        }
    }//GEN-LAST:event_txChatMessageKeyPressed

    private void btnSendMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnSendMouseClicked
        String chatMsg = txChatMessage.getText();
        txChatMessage.setText("");

        if (!chatMsg.equals("")) {
            Client.socketHandler.sendChat(chatMsg, user, roomID);
        }
    }//GEN-LAST:event_btnSendMouseClicked

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        StringSelection stringSelection = new StringSelection (roomId.getText());
        Clipboard clpbrd = Toolkit.getDefaultToolkit ().getSystemClipboard ();
        clpbrd.setContents (stringSelection, null);
    }//GEN-LAST:event_jButton1ActionPerformed

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
    private javax.swing.JButton btnSend;
    private javax.swing.JButton btnStartNewGame;
    private javax.swing.JPanel chatPane;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lbMoveNumber;
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
