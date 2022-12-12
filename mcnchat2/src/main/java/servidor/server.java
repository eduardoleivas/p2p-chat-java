package main.java.servidor;

import main.java.servidor.util.ClientHandler;
import main.java.servidor.util.ServerThread;
import java.net.*;
import java.io.*;
import java.util.ArrayList;

public class server {

    private static ArrayList<ClientHandler> clients = new ArrayList<>();

    public static int connectClients(int port, String sUsername, String fUsername) {
        try {
            // -- GET CLIENT HANDLERS -- //
            ClientHandler server = findClient(sUsername);
            ClientHandler amigo = findClient(fUsername);

            // -- GET & MOUNT SERVER INFO -- //
            String sIp = server.getIp();
            String sPort = Integer.toString(port);
            System.out.println(sIp + " | " + port);

            // -- SEND CONNECT COMMAND TO FRIEND -- //
            amigo.getSt().sendMsg(sIp, sPort);
            return 1;
        } catch (IOException e) {
        }
        return 0;
    }

    public static void addClients(String username, String ip, ServerThread st) {
        ClientHandler client = new ClientHandler(username, ip, st);
        clients.add(client);

        for(int i = 0; i < clients.size(); i++) {
            System.out.println(clients.get(i).getIp());
        }
    }

    public static ClientHandler delClient(String username) {
        for(int i = 0; i < clients.size(); i++) {
            if(clients.get(i).getUsername().equals(username)) {
                clients.remove(i);
            }
        }
        return null;
    }

    private static ClientHandler findClient(String username) {
        for(int i = 0; i < clients.size(); i++) {
            if(clients.get(i).getUsername().equals(username)) {
                return clients.get(i);
            }
        }
        return null;
    }

    public static void main(String s[]) throws Exception {
        Socket socket = null;             //SOCKET QUE SERÁ ATUALIZADO PARA CADA CLIENTE
        ServerSocket serverSocket = null; //SOCKET QUE SERÁ INICIALIZADO PARA O SERVIDOR
        try {
            serverSocket = new ServerSocket(4445); //SOCKET PRINCIPAL DO SERVIDOR
            System.out.println("Aguardando conexão");
        } catch(IOException e) {
            System.out.println("Erro ao iniciar o main.java.servidor");
            e.printStackTrace();
        }

        //ENQUANTO O SERVIDOR ESTIVER ABERTO
        while(true) {
            try {
                socket = serverSocket.accept();             //ACEITA A CONEXÃO DO CLIENTE
                ServerThread st = new ServerThread(socket); //CRIA UM NOVO THREAD COM O CLIENTE CONECTADO
                st.start();                                 //INICIA O THREAD DO SOCKET CLIENTE(X) - SERVIDOR
                System.out.println("Conectado com sucesso");
            } catch (IOException e) {
                System.out.println("Erro de Conexão");
                e.printStackTrace();

            }
        }
    }
}
