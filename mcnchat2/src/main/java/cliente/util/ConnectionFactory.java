package main.java.cliente.util;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class ConnectionFactory {
    private static Socket client; //CRIA UM SOCKET
    private static ServerSocket sClient; //SOCKET DE CLIENT-SERVER
    private static Socket cClient;       //SOCKET DE CLIENT-CHAT

    //INICIA A CONEXÃO
    public static void connectionFactory(String host, int port) {
        client = createSocket(host, port);
        System.out.println("Conectado com Sucesso!");
    }

    //INICIA A CONEXÃO
    public static void createCSocket(String host, int port) {
        cClient = createSocket(host, port);
        System.out.println("Conectado com Sucesso!");
    }

    //CRIA O SOCKET DE CONEXÃO DO CLIENTE
    private static Socket createSocket(String host, int port) {
        Socket temp = null;
        try {
            temp = new Socket(host, port);

        } catch (UnknownHostException e) {
            System.out.println("Não foi possível conectar-se ao servidor.");
            //e.printStackTrace();
        } catch (IOException e) {
            //e.printStackTrace();
        }
        return temp;
    }

    public static void createServer(int port) {
        ServerSocket serverSocket = null; //SOCKET QUE SERÁ INICIALIZADO PARA O CLIENT-SERVER
        try {
            serverSocket = new ServerSocket(port); //SOCKET DO CLIENT-SERVER
            sClient = serverSocket;
            System.out.println(sClient.getInetAddress() + " | " + port);
            System.out.println("Server Criado");
        } catch (IOException e) {
            System.out.println("Erro ao iniciar o main.java.servidor");
            e.printStackTrace();
        }
    }
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
