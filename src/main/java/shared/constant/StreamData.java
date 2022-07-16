/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package shared.constant;

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
        // security
        AESKEY, // client gửi aes key cho server / aeskey đã được mã hóa / server không cần phản hồi

        // auth
        SIGNAL_CHECKLOGIN, // chức năng đăng nhập / email, password / success hoặc failed
        SIGNAL_CREATEUSER, // chức năng đăng ký / thông tin đăng ký / success hoặc failed
        LOGOUT, // chức năng đăng xuất / không cần dữ liệu thêm / success hoặc failed
        UNKNOW_TYPE,
        NULL,
        EXIT
    }
    
    // https://stackoverflow.com/a/6667365
    public static Type getType(String typeName) {
        Type result = Type.UNKNOW_TYPE;

        try {
            result = Enum.valueOf(StreamData.Type.class, typeName);
        } catch (Exception e) {
            System.err.println("Unknow type: " + e.getMessage());
        }

        return result;
    }
    
    public static Type getTypeFromData(String data) {
        String typeStr = data.split("#")[1];
        return getType(typeStr);
    }
}
