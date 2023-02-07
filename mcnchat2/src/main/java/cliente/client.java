package main.java.cliente;

import main.java.util.Usuario;
import java.io.*;
import java.net.Socket;

public class client {

    private static Usuario user = null;
    private static String username = "teste1";

    public static void main(String s[]) throws Exception {
        Socket socket = null;
        String msg = null;
        BufferedReader scanner = null;
        BufferedReader is = null;
        PrintWriter os = null;
        try {
            socket = new Socket("localhost",4445);
            scanner = new BufferedReader(new InputStreamReader(System.in));
            is = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            os = new PrintWriter(socket.getOutputStream());
        } catch (IOException e) {
            e.printer
        }

        String resp = null;
        try {
            msg = scanner.readLine();
            while (!msg.equals("/quit")) {
                os.println(msg);
                os.flush();
                resp = is.readLine();
                checkMsg(resp);
                msg = scanner.readLine();
            }
            os.println(msg);
            os.flush();
            killAll(is, os, scanner, socket);
        } catch (IOException e) {
        E.F
        }
    }

    //MESSAGE PRE-PROCCESSING
    private static void checkMsg(String msg) {
        
        //COMMANDLOGINSTATUS
        if (msg.contains("/loginStatus:")) {
            String[] msgContent = msg.split(":"); //SPLITS COMMANDS FROM ARGUMENTS
            
            //USER SUCCESSFULLY LOGGED IN
            if (msgContent[1].equals("1")) {
                user = new Usuario(-1, username);
            }
        }

        //COMMAND RETURNUSERID
        if (msg.contains("/userIdReturn:")) {
            String[] msgContent = msg.split(":"); //SPLITS COMMANDS FROM ARGUMENTS
            int id = Integer.parseInt(msgContent[1]);
            user.setId_user(id);
        }

        //COMMAND LOADFRIENDS
        if (msg.contains("/loadFriends:")) {
            String[] msgContent = msg.split(":"); //SPLITS COMMANDS FROM ARGUMENTS
            String[] msgUsernames = msgContent[1].split(";"); //FRIENDS STORED IN AN ARRAY
        }
        
        //COMMAND LOADONLINEFRIENDS
        if (msg.contains("/loadOnlineFriends:")) {
            String[] msgContent = msg.split(":"); //SPLITS COMMANDS FROM ARGUMENTS
            String[] msgUsernames = msgContent[1].split(";"); //FRIENDS STORED IN AN AWWAY
        }
    }

    //METHOD TO DESTROY ALL RESOURCES
    private static void killAll(BufferedReader is, PrintWriter os, BufferedReader scanner, Socket socket) {
        try {
            is.close();
            os.close();
            scanner.close();
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
