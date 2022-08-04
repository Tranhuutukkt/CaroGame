/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Project/Maven2/JavaApp/src/main/java/${packagePath}/${mainClassName}.java to edit this template
 */

package client;

import client.controller.SocketHandler;
import client.view.helper.LookAndFeel;
import client.view.scene.ConnectServer;
import client.view.scene.Login;
import client.view.scene.Signup;
import client.view.scene.Menu;
import client.view.scene.Room;
import client.view.scene.Leaderboard;


public class Client {

    public enum SceneName {
        CONNECTSERVER,
        LOGIN,
        SIGNUP,
        MENU,
        ROOM,
        LEADERBOARD
    }

    // scenes
    public static ConnectServer connectServerScene;
    public static Login loginScene;
    public static Signup signupScene;
    public static Menu menuScene;
    public static Room roomScene;
    public static Leaderboard leaderboardScene;

    // controller 
    public static SocketHandler socketHandler;

    public Client() {
        socketHandler = new SocketHandler();
        initScene();
        openScene(SceneName.CONNECTSERVER);
    }

    public void initScene() {
        connectServerScene = new ConnectServer();
        loginScene = new Login();
        signupScene = new Signup();
        menuScene = new Menu();
        roomScene = new Room();
        leaderboardScene = new Leaderboard();
    }

    public static void openScene(SceneName sceneName) {
        if (null != sceneName) {
            switch (sceneName) {
                case CONNECTSERVER:
                    // tạo lại scene để tạo lại state mặc định
                    // nếu chỉ setVisible(true) thì cũng open được scene cũ, nhưng state thì không phải mặc định
                    connectServerScene = new ConnectServer();
                    connectServerScene.setVisible(true);
                    break;
                case LOGIN:
                    loginScene = new Login();
                    loginScene.setVisible(true);
                    break;
                case SIGNUP:
                    signupScene = new Signup();
                    signupScene.setVisible(true);
                    break;
                case MENU:
                    menuScene = new Menu();
                    menuScene.setVisible(true);
                    break;
                case ROOM:
                    roomScene = new Room();
                    roomScene.setVisible(true);
                    break;
                case LEADERBOARD:
                    leaderboardScene = new Leaderboard();
                    leaderboardScene.setVisible(true);
                    break;
                default:
                    break;
            }
        }
    }

    public static void closeScene(SceneName sceneName) {
        if (null != sceneName) {
            switch (sceneName) {
                case CONNECTSERVER:
                    connectServerScene.dispose();
                    break;
                case LOGIN:
                    loginScene.dispose();
                    break;
                case SIGNUP:
                    signupScene.dispose();
                    break;
                case MENU:
                    menuScene.dispose();
                    break;
                case ROOM:
                    roomScene.dispose();
                    break;
                case LEADERBOARD:
                    leaderboardScene.dispose();
                    break;
                default:
                    break;
            }
        }
    }

    public static void closeAllScene() {
        connectServerScene.dispose();
        loginScene.dispose();
        signupScene.dispose();
        menuScene.dispose();
        roomScene.dispose();
        leaderboardScene.dispose();
    }

    public static void main(String[] args) {
        LookAndFeel.setNimbusLookAndFeel();
        new Client();
    }
}