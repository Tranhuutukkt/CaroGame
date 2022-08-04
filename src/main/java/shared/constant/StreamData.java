/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package shared.constant;

import java.util.Arrays;

/**
 *
 * @author trantu4120
 */
public class StreamData {
    /* Cách đọc hiểu đống bên dưới?
        => Đống bên dưới được viết comment theo cấu trúc sau:
    
        Tên type // mô tả / dữ liệu gửi đi từ client / dữ liệu trả về từ server
     */
    public enum Type {
        SIGNAL_OK,          //connect server successfully
        SIGNAL_CHECKLOGIN, // chức năng đăng nhập / email, password / success hoặc failed
        SIGNAL_CREATEUSER, // chức năng đăng ký / thông tin đăng ký / success hoặc failed
        SIGNAL_MENU,      //show menu
        SIGNAL_LOGOUT, // chức năng đăng xuất / không cần dữ liệu thêm / success hoặc failed
        CREATE_ROOM,  //tao phong
        JOIN_ROOM,     //vao phong bang ID
        LEAVE_ROOM,     //roi khoi room
        START_GAME,     //bat dau tro choi
        GAME_MOVE,      //tao nuoc di
        GAME_WIN,       //thong bao nguoi thang
        SET_POINT,       //caculate point
        CHAT,           //send chat
        RANKING,        //leaderboard
        USER_INFO,       //user info in leaderboard
        UNKNOW_TYPE,
        NULL,
        SIGNAL_CLOSE,       //Close fd
    }
    
    // https://stackoverflow.com/a/6667365
    public static Type getType(String typeName) {
        Type result = Type.UNKNOW_TYPE;

        try {
            result = Enum.valueOf(StreamData.Type.class, typeName);
            
        } catch (Exception e) {
            System.err.println("Unknow type: " + e.getMessage()+"abc");
        }

        return result;
    }
    
    public static Type getTypeFromData(String data) {
        String typeStr = data.split("#")[0].trim();
        return getType(typeStr);
    }
}
