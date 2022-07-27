/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package client.controller;

import client.Client;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import shared.constant.StreamData;

/**
 *
 * @author trantu4120
 */
public class SocketHandler {
    Socket s;
    BufferedReader dis;
    BufferedWriter dos;

    String user = null; // lưu tài khoản đăng nhập hiện tại
    String opUser = null; //luu ti khoan doi thu
    String roomId = null; //luu roomId
    Thread listener = null;
    
    public String connect(String addr, int port) {
        try {
//            // getting ip 
//            InetAddress ip = InetAddress.getByName(addr);

            // establish the connection with server port 
            s = new Socket(addr, port);
            //s.connect(new InetSocketAddress(ip, port), 4000);
            System.out.println("Connected to " + addr + ":" + port + ", localport:" + s.getLocalPort());

            // obtaining input and output streams
            dis = new BufferedReader(new InputStreamReader(s.getInputStream()));
            dos = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));

            // close old listener
            if (listener != null && listener.isAlive()) {
                listener.interrupt();
            }

            // listen to server
            listener = new Thread(this::listen);
            listener.start();

            // connect success
            return "success";

        } catch (IOException e) {

            // connect failed
            return "failed;" + e.getMessage();
        }
    }
    
    private void listen() {
        boolean running = true;

        while (running) {
            try {
                // receive the data from server
                String received = dis.readLine();

                System.out.println("RECEIVED: " + received + "abc" + received.length());
                System.out.println("received: " + Arrays.toString(received.getBytes()) + "abc");

                // process received data
                StreamData.Type type = StreamData.getTypeFromData(received);

                switch (type) {

                    case SIGNAL_CHECKLOGIN:
                        onReceiveLogin(received);
                        break;

                    case SIGNAL_CREATEUSER:
                        onReceiveSignup(received);
                        break;
                        
                    case SIGNAL_MENU:
                        showMenu(received);
                        break;

                    case SIGNAL_LOGOUT:
                        onReceiveLogout();
                        break;
                        
                    case CREATE_ROOM:
                        onReceiveCreateRoom(received);
                        break;
                    
                    case JOIN_ROOM:
                        onReceiveJoinRoom(received);
                        break;
                        
                    case NULL:
                        break;

                    case EXIT:
                        running = false;
                        
                    default:
                        break;
                }

            } catch (IOException ex) {
                Logger.getLogger(SocketHandler.class.getName()).log(Level.SEVERE, null, ex);
                running = false;
            }
        }

        try {
            // closing resources
            s.close();
            dis.close();
            dos.close();
        } catch (IOException ex) {
            Logger.getLogger(SocketHandler.class.getName()).log(Level.SEVERE, null, ex);
        }

        // alert if connect interup
        JOptionPane.showMessageDialog(null, "Mất kết nối tới server", "Lỗi", JOptionPane.ERROR_MESSAGE);
        Client.closeAllScene();
        Client.openScene(Client.SceneName.CONNECTSERVER);
    }
    
    private void onReceiveLogin(String received) {
        // get status from data
        String[] splitted = received.split("#");
        String status = splitted[1];
        System.out.println("Status: " + status);

        if (status.equals("error")) {
      
      // hiển thị lỗi
            String failedMsg = splitted[2];
            JOptionPane.showMessageDialog(Client.loginScene, failedMsg, "Lỗi", JOptionPane.ERROR_MESSAGE);

        } else if (status.equals("ok")) {
            // lưu user login
            this.user = splitted[2];

            // chuyển scene
            Client.closeScene(Client.SceneName.LOGIN);
            Client.openScene(Client.SceneName.MENU);
        }
    }
    
    private void onReceiveSignup(String received) {
        // get status from data
        String[] splitted = received.split("#");
        String status = splitted[1];

        // check status
        if (status.equals("error")) {
            String failedMsg = splitted[2];
            JOptionPane.showMessageDialog(Client.signupScene, failedMsg, "Error", JOptionPane.ERROR_MESSAGE);

        } else if (status.equals("ok")) {
            JOptionPane.showMessageDialog(Client.signupScene, "Đăng ký thành công", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            Client.closeScene(Client.SceneName.SIGNUP);
            Client.openScene(Client.SceneName.LOGIN);
        }
    }
    
    private void onReceiveLogout() {
        // xoa user
        this.user = null;

        // chuyển scene
        Client.closeAllScene();
        Client.openScene(Client.SceneName.LOGIN);
    }
    
    private void onReceiveCreateRoom(String received) {
        String[] splitted = received.split("#");
        this.roomId = splitted[1];

        // chuyển scene
        Client.closeAllScene();
        Client.openScene(Client.SceneName.ROOM);
    }
    
    private void onReceiveJoinRoom(String received) {
        String[] splitted = received.split("#");
        String status = splitted[1];
        
        // check status
        if (status.equals("error")) {
            String failedMsg = splitted[2];
            JOptionPane.showMessageDialog(Client.menuScene, failedMsg, "Error", JOptionPane.ERROR_MESSAGE);

        } else if (status.equals("ok")) {
            opUser = (splitted[2].equals(user)) ? splitted[3] : splitted[2];
            String successMsg = (splitted[2].equals(user)) ? "You" : splitted[2] + "joined the room successfully!";
            Client.closeAllScene();
            Client.openScene(Client.SceneName.ROOM);
            
            JOptionPane.showMessageDialog(Client.roomScene, successMsg, "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void showMenu(String received) {
        // chuyển scene
        Client.openScene(Client.SceneName.MENU);
    }
    
    public void login(String user, String password) {

        // prepare data
        String data = StreamData.Type.SIGNAL_CHECKLOGIN.name() + "#" + user + "#" + password;
        System.out.println("Login: " + data);
        // send data
        sendData(data);
    }
    
    public void signup(String user, String password) {
        // prepare data
        String data = StreamData.Type.SIGNAL_CREATEUSER.name() + "#"
                + user + "#"
                + password;

        // send data
        sendData(data);
    }
    
    public void logout(String user) {
        // prepare data
        String data = StreamData.Type.SIGNAL_LOGOUT.name() + "#" + user;

        // send data
        sendData(data);
    }
    
    public void createRoom(String user) {
        // prepare data
        String data = StreamData.Type.CREATE_ROOM.name() + "#" + user;

        // send data
        sendData(data);
    }
    
    public void joinRoom(String user, String roomId) {
        this.roomId= roomId;
        // prepare data
        String data = StreamData.Type.JOIN_ROOM.name() + "#" + user + "#" + roomId;

        // send data
        sendData(data);
    }
    
    public void sendData(String data) {
        try {
            dos.write(data);
            //dos.newLine(); // kết thúc dòng
            dos.flush(); 

        } catch (IOException ex) {
            Logger.getLogger(SocketHandler.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public String getUser() {
        return user;
    }
    
    public String getOpUser() {
        return opUser;
    }
    
    public String getRoom() {
        return roomId;
    }
    
    public String setRoom(String roomId) {
        this.roomId = roomId;
        return null;
    }
}
