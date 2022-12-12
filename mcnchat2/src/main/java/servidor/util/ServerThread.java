package main.java.servidor.util;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import main.java.servidor.server;
import main.java.util.Usuario;
import main.java.util.dbManager;

import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class ServerThread extends Thread {
    String line = null;
    BufferedReader is = null;
    PrintWriter os = null;
    Socket s1 = null;
    Connection conn = null;
    Usuario user = null;

    public ServerThread(Socket s) {
        s1 = s;
        this.conn = dbManager.dbConnect();
    }

    public void run() {
        try {
            is = new BufferedReader(new InputStreamReader(s1.getInputStream()));
            os = new PrintWriter(s1.getOutputStream());
            line = is.readLine();
            while (s1.isConnected()) {
                while(!line.equals("/quit")) {
                        checkMsg(line);
                    }
                }
            server.delClient(user.getUsername());
            try {
                dbManager.dbStatusUpdate(conn, 0, user.getUsername());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            killAll();
            this.destroy();
        } catch(IOException | SQLException ie) {
            server.delClient(user.getUsername());
            try {
                dbManager.dbStatusUpdate(conn, 0, user.getUsername());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            ie.printStackTrace();
            this.destroy();
        }
    }

    private void checkMsg(String msg) throws SQLException {
        //COMANDO GETFRIENDS
        if(msg.contains("/getFriends:")) {
            try {
                String[] msgContent = msg.split(":");
                String username = msgContent[1];
                int id = user.getId_user();
                System.out.println("ID getFriends: " + id);
                List<Usuario> amigos = dbManager.dbGetFriends(this.conn, "id_amigo", "contatos", "id_user", id);
                int size = amigos.size();
                String resp = "/loadFriends:";
                if(amigos.size() != 0) {
                    for(int i=0; i<size; i++) {
                        Usuario temp = amigos.get(i); //RECEBE O USUARIO NA POSIÇÃO X
                        resp = resp.concat(temp.getUsername() + ";");
                    }
                }
                os.println(resp);
                os.flush();
                resp = is.readLine();
                line = resp;
            } catch (IOException e) {
                try {
                    server.delClient(user.getUsername());
                    try {
                        dbManager.dbStatusUpdate(conn, 0, user.getUsername());
                    } catch (SQLException e2) {
                        throw new RuntimeException(e2);
                    }
                    killAll();
                    throw new RuntimeException(e);
                } catch (IOException ex) {
                    server.delClient(user.getUsername());
                    throw new RuntimeException(ex);
                }
            }
        }

        //COMANDO GETONLINEFRIENDS
        if(msg.contains("/getOnlineFriends:")) {
            try {
                String[] msgContent = msg.split(":");
                String username = msgContent[1];
                int id = user.getId_user();
                System.out.println("ID getOnlineFriends: " + id);
                List<Usuario> amigos = dbManager.dbGetOnlineFriends(this.conn, id);
                int size = amigos.size();
                String resp = "/loadOnlineFriends:";
                for(int i=0; i<size; i++) {
                    Usuario temp = amigos.get(i); //RECEBE O USUARIO NA POSIÇÃO X
                    resp = resp.concat(temp.getUsername() + ";");
                }
                os.println(resp);
                os.flush();
                resp = is.readLine();
                line = resp;
            } catch (IOException e) {
                try {
                    server.delClient(user.getUsername());
                    dbManager.dbStatusUpdate(conn, 0, user.getUsername());
                    killAll();
                    throw new RuntimeException(e);
                } catch (IOException ex) {
                    server.delClient(user.getUsername());
                    throw new RuntimeException(ex);
                }
            }
        }

        //COMANDO LOGINREQUEST
        if(msg.contains("/loginRequest:")) {
            if(user == null) {
                try {
                    String[] msgContent = msg.split(":");
                    String[] credentials = msgContent[1].split(";");
                    String username = credentials[0];
                    String pass = credentials[1];
                    boolean login = dbManager.dbLogin(this.conn, username, pass);
                    System.out.println(login);
                    String resp;
                    if(login == true) {
                        resp = "/loginStatus:1";
                        dbManager.dbStatusUpdate(conn, 1, username);
                    } else {
                        resp = "/loginStatus:0";
                    }
                    user = new Usuario(-1, username); //ID PROVISÓRIO
                    addClientToArray();
                    os.println(resp);
                    os.flush();
                    resp = is.readLine();
                    line = resp;
                } catch (IOException e) {
                    try {
                        server.delClient(user.getUsername());
                        killAll();
                        throw new RuntimeException(e);
                    } catch (IOException ex) {
                        server.delClient(user.getUsername());
                        throw new RuntimeException(ex);
                    }
                }
            } else {
                try {
                    String resp = "/loginStatus:2";
                    os.println(resp);
                    os.flush();
                    resp = is.readLine();
                    line = resp;
                } catch (IOException e) {
                    try {
                        killAll();
                        throw new RuntimeException(e);
                    } catch (IOException ex) {
                        server.delClient(user.getUsername());
                        throw new RuntimeException(ex);
                    }
                }
            }
        }

        //COMANDO GETUSERID
        if(msg.contains("/userIdRequest:")) {
            try {
                String[] msgContent = msg.split(":");
                String username = msgContent[1];
                int id = dbManager.dbGetIdByUsername(this.conn, "id_user", "usuario", "username", username);
                System.out.println(id);
                user.setId_user(id);
                String resp = "/userIdReturn:" + id;
                os.println(resp);
                os.flush();
                resp = is.readLine();
                line = resp;
            } catch (IOException e) {
                try {
                    server.delClient(user.getUsername());
                    killAll();
                    throw new RuntimeException(e);
                } catch (IOException ex) {
                    server.delClient(user.getUsername());
                    throw new RuntimeException(ex);
                }
            }
        }

        //COMANDO REQUESTFRIENDCONNECTION
        if(msg.contains("/requestFriendConnection:")) {
            try {
                // -- SPLIT STRING CONTENTS -- //
                String[] msgContent = msg.split(":");
                String[] msgDivide = msgContent[1].split(";");

                // -- GET CONNECTION DATA -- //
                int port = Integer.parseInt(msgDivide[0]);
                String sUser = msgDivide[1];
                String fUser = msgDivide[2];

                // -- SEND CONNECTION REQUEST TO SERVER -- //
                final int[] foundFriend = new int[1];
                Thread connect = new Thread(() -> {
                    foundFriend[0] = server.connectClients(port, sUser, fUser);
                });
                connect.start();
                String resp = "1";
                System.out.println("FCS: " + foundFriend[0]);
                // -- SEND ANSWER AND FLUSH INPUTSTREAM -- //
                os.println(resp);
                os.flush();
                resp = is.readLine();
                line = resp;
            } catch (IOException e) {
                try {
                    server.delClient(user.getUsername());
                    killAll();
                    throw new RuntimeException(e);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }

        //COMANDO DELCLIENT
        if(msg.contains("/delClient:")) {
            // -- SPLIT STRING CONTENTS -- //
            String[] msgContent = msg.split(":");
            server.delClient(msgContent[1]);
            System.out.println(msg);
                String resp = "/clientDeleted:1";
                os.println(resp);
                os.flush();
            try {
                resp = is.readLine();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            line = resp;
        }
    }

    //ADD CLIENT TO ARRAY
    private void addClientToArray() {
        String username = user.getUsername();
        InetSocketAddress sockaddr = (InetSocketAddress)s1.getRemoteSocketAddress();
        String ip = sockaddr.getAddress().getHostAddress();
        server.addClients(user.getUsername(), ip, this);
    }

    public void sendMsg(String ip, String port) throws IOException {
        String resp = "/connectToUserServer:"+ip+";"+port;
        System.out.println(resp);
        os.println(resp);
        os.flush();
        resp = is.readLine();
        line = resp;
    }

    //KILL ALL
    private void killAll() throws IOException, SQLException {
        is.close();
        os.close();
        s1.close();
        this.conn.close();
    }
}