package main.java.cliente.util;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class ConnectionFactory {
    private static Socket client;        //CREATES A SOCKET
    private static ServerSocket sClient; //SOCKET FOR CLIENT-SERVER
    private static Socket cClient;       //SOCKET FOR CLIENT-CHAT

    //STARTS SERVER CONNECTION
    public static void connectionFactory(String host, int port) {
        client = createSocket(host, port);
    }

    //STARTS CLIENT-CHAT CONNECTION
    public static void createCSocket(String host, int port) {
        cClient = createSocket(host, port);
    }

    //CRIA O SOCKET DE CONEXÃO DO CLIENTE
    private static Socket createSocket(String host, int port) {
        Socket temp = null;
        try {
            temp = new Socket(host, port);

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return temp;
    }

    //METHOD CREATE A SERVER
    public static void createServer(int port) {
        ServerSocket serverSocket = null; //SOCKET QUE SERÁ INICIALIZADO PARA O CLIENT-SERVER
        try {
            serverSocket = new ServerSocket(port); //SOCKET DO CLIENT-SERVER
            sClient = serverSocket;
        
        //INITIALIZATION ERROR
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    //GETTERS
    public static ServerSocket getServer() {
        return sClient;
    }
    
    public static Socket getChat() {
        return cClient;
    }

    public static Socket getConnection() {
        return client;
    }
}
