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

            // -- SEND CONNECT COMMAND TO FRIEND -- //
            amigo.getSt().sendMsg(sIp, sPort);
            return 1;
        } catch (IOException e) {
            e.printstacktrace();
        }
        return 0;
    }

    //METHOD ADD CLIENTS TO CLIENTHANDLER
    public static void addClients(String username, String ip, ServerThread st) {
        ClientHandler client = new ClientHandler(username, ip, st);
        clients.add(client);
    }

    //METHOD DELETE CLIENTHANDLER
    public static ClientHandler delClient(String username) {
        for(int i = 0; i < clients.size(); i++) {
            if(clients.get(i).getUsername().equals(username)) {
                clients.remove(i);
            }
        }
        return null;
    }

    //METHOD FIND CLIENT'S INFOS
    private static ClientHandler findClient(String username) {
        for(int i = 0; i < clients.size(); i++) {
            if(clients.get(i).getUsername().equals(username)) {
                return clients.get(i);
            }
        }
        return null;
    }

    //MAIN METHOD 
    public static void main(String s[]) throws Exception {
        Socket socket = null;             //SOCKET THAT WILL BE UPDATED FOR THE CLIENT
        ServerSocket serverSocket = null; //SOCKET THAT WILL BE UPDATED FOR THE SERVER
        try {
            serverSocket = new ServerSocket(4445); //SERVER'S MAIN SOCKET
        } catch(IOException e) {
            e.printStackTrace();
        }

        //WHILE SERVER IS ACTIVE
        while(true) {
            try {
                socket = serverSocket.accept();             //ACCEPTS CLIENT'S CONNECTION REQUEST
                ServerThread st = new ServerThread(socket); //CREATES A NEW THREAAD WITH THE CONNECTED CLIENT
                st.start();                                 //INITIALIZES THE CLIENT(X)'S SOCKET - SERVER
            } catch (IOException e) {
                e.printStackTrace();

            }
        }
    }
}
