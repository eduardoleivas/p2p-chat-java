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
            System.err.print("IO Exception");
        }

        System.out.println("Mensagem: ");
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
            System.out.println("Erro no socket");
        }
    }

    private static void checkMsg(String msg) {
        //COMANDO LOGINSTATUS
        if (msg.contains("/loginStatus:")) {
            String[] msgContent = msg.split(":"); //SEPARA O COMANDO DOS ARGUMENTOS
            if (msgContent[1].equals("1")) {
                //USUARIO LOGADO COM SUCESSO
                    //DB GET ID BY USERNAME
                    //CARREGAR A FRIEND LIST
                    //SET STATUS ONLINE
                user = new Usuario(-1, username);
                System.out.println(msgContent[1]);
                System.out.println("Logado com Sucesso");
            } else if (msgContent[1].equals("0")) {
                //LOGIN INCORRETO
                System.out.println(msgContent[1]);
                System.out.println("Login Incorreto");
            } else {
                //USUÁRIO JÁ LOGADO
                System.out.println("Usuário já logado");
            }
        }

        //COMANDO RETURNUSERID
        if (msg.contains("/userIdReturn:")) {
            String[] msgContent = msg.split(":"); //SEPARA O COMANDO DOS ARGUMENTOS
            int id = Integer.parseInt(msgContent[1]);
            user.setId_user(id);
            System.out.println("Seu ID é: " + id);
        }

        //COMANDO LOADFRIENDS
        if (msg.contains("/loadFriends:")) {
            String[] msgContent = msg.split(":"); //SEPARA O COMANDO DOS ARGUMENTOS
            String[] msgUsernames = msgContent[1].split(";"); //ARRAY COM OS AMIGOS
            for(int i=0; i<msgUsernames.length; i++) {
                System.out.println(msgUsernames[i]);
            }
        }
        //COMANDO LOADONLINEFRIENDS
        if (msg.contains("/loadOnlineFriends:")) {
            String[] msgContent = msg.split(":"); //SEPARA O COMANDO DOS ARGUMENTOS
            String[] msgUsernames = msgContent[1].split(";"); //ARRAY COM OS AMIGOS
            for(int i=0; i<msgUsernames.length; i++) {
                System.out.println(msgUsernames[i]);
            }
        }
    }

    private static void killAll(BufferedReader is, PrintWriter os, BufferedReader scanner, Socket socket) {
        try {
            is.close();
            os.close();
            scanner.close();
            socket.close();
            System.out.println("Conexão Finalizada.");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}