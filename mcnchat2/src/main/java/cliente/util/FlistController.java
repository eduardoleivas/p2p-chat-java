package main.java.cliente.util;

import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import main.java.util.Usuario;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.Random;

public class FlistController {
    @javafx.fxml.FXML
    private AnchorPane basePane;
    @javafx.fxml.FXML
    private ScrollPane lstScroll;
    @javafx.fxml.FXML
    private AnchorPane lstBox;
    @javafx.fxml.FXML
    private Label lblUser;
    @javafx.fxml.FXML
    private Label lblFriendDefault;
    @javafx.fxml.FXML
    private Label lblFriendStatusDefault;
    @javafx.fxml.FXML
    private Line lineDefault;
    @javafx.fxml.FXML
    private Button btnRefresh;
    @javafx.fxml.FXML
    private Label lblIp;

    private Socket socket = null;
    private String msg = null;
    private BufferedReader is = null;
    private PrintWriter os = null;
    private String resp = null;
    private Usuario user = ClientController.getUser();
    private String allFriends = null;
    private String onlineFriends = null;
    private int counter;
    private String lastMsg = "default";
    private int port;

    public void initialize() throws IOException {
        try {
            socket = ConnectionFactory.getConnection();
            is = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            os = new PrintWriter(socket.getOutputStream());
            startFlist(is, os);
            Thread clientListener = new Thread(() -> {
                try {
                    while (true) {
                        String clientMsg = is.readLine(); //LÊ A MENSAGEM NO INPUT STREAM (CHARSET UTF)
                        if (!clientMsg.equals(null)) {
                            if(!clientMsg.equals(lastMsg)) {
                                lastMsg = clientMsg;
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        checkMsg(clientMsg);//EXIBE A MENSAGEM NA TELA
                                        System.out.println(clientMsg);
                                    }
                                });
                            }
                        }
                    }
                } catch(IOException e){
                    e.printStackTrace();
                }
            });
            clientListener.start();
        } catch (IOException e) {
            System.err.print("IO Exception");
            killAll(socket, is, os);
        }
    }
    //LOGIN REQUEST ----
    //DB GET ID BY USERNAME ----
    //CARREGAR A FRIEND LIST
    //SET STATUS ONLINE
    private void startFlist(BufferedReader is, PrintWriter os) {
        // -- LOGIN REQUEST -- //
        sendMsg(os, "/loginRequest:"+user.getUsername()+";"+user.getPassword());
        // -- LOGIN CONFIRMED -- //

        // -- GET ID BY USERNAME -- //
        sendMsg(os, "/userIdRequest:"+user.getUsername());
        // -- USER OBJECT COMPLETE -- //

        lblIp.setText(String.valueOf(socket.getInetAddress()));
    }

    private void sendMsg(PrintWriter os, String msg) {
        os.println(msg);
        os.flush();
    }

    private void checkMsg(String msg) {
        //COMANDO LOGINSTATUS
        if (msg.contains("/loginStatus:")) {
            String[] msgContent = msg.split(":"); //SEPARA O COMANDO DOS ARGUMENTOS

            //USUARIO LOGADO COM SUCESSO
            if (msgContent[1].equals("1")) {
                lblUser.setText(user.getUsername());    //EXIBE O NOME DO USUARIO NA FLIST
                user.setPassword(null);                 //NAO GUARDA MAIS A SENHA DO USUARIO
                System.out.println("Logado com Sucesso");

            //LOGIN INCORRETO
            } else if (msgContent[1].equals("0")) {
                System.out.println("Login Incorreto");

            //USUÁRIO JÁ LOGADO
            } else {
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
            if(msgContent.length > 1) {
                allFriends = msgContent[1];
                System.out.println("O CARA TEM AMIGO");
            } else {
                allFriends = null;
            }
        }
        //COMANDO LOADONLINEFRIENDS
        if (msg.contains("/loadOnlineFriends:")) {
            String[] msgContent = msg.split(":"); //SEPARA O COMANDO DOS ARGUMENTOS
            if(msgContent.length > 1) {
                onlineFriends = msgContent[1];
                System.out.println("O CARA TEM AMIGO ON");
            } else {
                onlineFriends = null;
            }
        }

        //COMANDO CONNECTTOUSER
        if (msg.contains("/connectToUserServer:")) {

            // -- SPLIT COMMAND CONTENTS -- //
            String[] msgContent = msg.split(":");
            String[] svInfo = msgContent[1].split(";");

            // -- GET SERVER INFO -- //
            String ip = svInfo[0];
            int port = Integer.parseInt(svInfo[1]);

            // -- MAKE CONNECTION -- //
            try {
                ConnectionFactory.createCSocket(ip, port);
                Stage stage2 = new Stage();
                Parent root2 = FXMLLoader.load(getClass().getResource("/cliente/chatGUI.fxml"));
                Scene scene2 = new Scene(root2);
                stage2.setTitle(user.getUsername());
                stage2.setResizable(false);
                stage2.setScene(scene2);
                stage2.show();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    //LOAD FRIENDS ON LIST
    private void loadFriends(BufferedReader is, PrintWriter os) {
        lstBox.getChildren().clear();
        lstBox.setPrefHeight(26);
        counter = 0;
        String[] online = null;
        String[] offline = null;
        // -- GET ALL FRIENDS -- //
        sendMsg(os, "/getFriends:"+user.getId_user()); //GET ALL FRIENDS

        // -- GET ONLINE FRIENDS -- //
        sendMsg(os, "/getOnlineFriends:"+user.getId_user()); //GET ALL FRIENDS

        // -- SPLIT FOR ONLINE FRIENDS USERNAMES -- //
        if(onlineFriends != null) {
            // -- FILTER RESULTS -- //
             online = onlineFriends.split(";");
        }

        // -- SEPARATE OFFLINE FRIENDS USERNAMES -- //
        if(allFriends != null) {
            System.out.println(allFriends);
            if(online != null) {
                for (int i = 0; i < online.length; i++) {
                    allFriends = allFriends.replace(online[i] + ";", "");
                    System.out.println(allFriends);
                }
            }
            offline = allFriends.split(";");
        }

        // -- CREATE LABELS -- //
        if(online != null) {
            for(int i = 0; i < online.length; i++) {
                createLabels(online[i], true);
            }
        }
        if(offline != null) {
            for (int i = 0; i < offline.length; i++) {
                createLabels(offline[i], false);
            }
        }
    }
    //ANCHORPANE + 26PX
    //NAME 6PX 4PX
    //STATUS 195PX 4PX
    //LINE 98PX 25PX
    private void createLabels(String username, boolean status) {
        // -- UPDATE ANCHORPANE SIZE -- //
        lstBox.setPrefHeight(lstBox.getHeight() + 26);

        if (status == true) {
            // -- CREATE USERNAME LABEL -- //
            Label lblFriendName = new Label();
            lblFriendName.setStyle(lblFriendDefault.getStyle());
            lstBox.getChildren().add(lblFriendName);
            lblFriendName.setLayoutX(6);
            lblFriendName.setLayoutY(4 + (counter * 26));
            lblFriendName.setText(username);
            lblFriendName.setOnMouseClicked(new EventHandler<MouseEvent>()
            {
                @Override
                public void handle(MouseEvent t) {
                    try {
                        friendConnect(lblFriendName.getText());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });

            // -- CREATE STATUS LABEL -- //
            Label lblFriendStatus = new Label();
            lblFriendStatus.setStyle(lblFriendStatusDefault.getStyle());
            lstBox.getChildren().add(lblFriendStatus);
            lblFriendStatus.setLayoutX(195);
            lblFriendStatus.setLayoutY(4 + (counter * 26));
            lblFriendStatus.setText("Online");
            lblFriendStatus.setTextFill(lblFriendStatusDefault.getTextFill());
        } else {
            // -- CREATE USERNAME LABEL -- //
            Label lblFriendName = new Label();
            lblFriendName.setStyle(lblFriendDefault.getStyle());
            lstBox.getChildren().add(lblFriendName);
            lblFriendName.setLayoutX(6);
            lblFriendName.setLayoutY(4 + (counter * 26));
            lblFriendName.setText(username);
            lblFriendName.setDisable(true);

            // -- CREATE STATUS LABEL -- //
            Label lblFriendStatus = new Label();
            lblFriendStatus.setStyle(lblFriendStatusDefault.getStyle());
            lstBox.getChildren().add(lblFriendStatus);
            lblFriendStatus.setLayoutX(195);
            lblFriendStatus.setLayoutY(4 + (counter * 26));
            lblFriendStatus.setText("Offline");
            lblFriendStatus.setDisable(true);
        }

        // -- CREATE DIVIDING LINE -- //
        Line fLine = new Line();
        fLine.setStroke(lineDefault.getStroke());
        lstBox.getChildren().add(fLine);
        fLine.setStartX(-190);
        fLine.setStartX(190);
        fLine.setLayoutX(25);
        fLine.setLayoutY(25 + (counter * 26));

        counter++; //UPDATE COUNTER
    }

    private void killAll(Socket socket, BufferedReader is, PrintWriter os) {
        try {
            msg = "/delClient:"+user.getUsername();
            os.println(msg);
            os.flush();
            is.close();
            os.close();
            socket.close();
            System.out.println("Conexão Finalizada.");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //STARTAR O CLIENT-SERVER NA PORTA X --
    //ENVIAR PORTA, USERNAME E ALVO --
    //CONECTAR O CLIENT NO SERVER
    //INICIAR O CHAT
    public void friendConnect(String fUsername) throws IOException {
        Random rand = new Random(System.currentTimeMillis());
        int port = rand.nextInt((65000 - 300)) + 300;
        ConnectionFactory.createServer(port); //STARTA O CLIENT SERVER NA PORTA X
        Thread chat = new Thread(() -> {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    try {
                        Stage stage3 = new Stage();
                        Parent root3 = FXMLLoader.load(getClass().getResource("/cliente/serverGUI.fxml"));
                        Scene scene3 = new Scene(root3);
                        stage3.setTitle(user.getUsername());
                        stage3.setResizable(false);
                        stage3.setScene(scene3);
                        stage3.show();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        });
        chat.start();
        //REQUEST DE CONEXAO PARA OUTRO CLIENTE
        sendMsg(os, "/requestFriendConnection:"+port+";"+user.getUsername()+";"+fUsername);

    }

    @javafx.fxml.FXML
    public void refreshFriends(ActionEvent actionEvent) {
        loadFriends(is, os);
    }
}
