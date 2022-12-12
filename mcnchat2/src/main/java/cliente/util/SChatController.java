package main.java.cliente.util;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

public class SChatController {

    @FXML
    private Label lblBase;
    @FXML
    private ImageView imgBg;
    @FXML
    private ScrollPane msgScroll;
    @FXML
    private AnchorPane msgBox;
    @FXML
    private TextArea txtField;
    @FXML
    private Button btnQuit;
    @FXML
    private Button btnSend;

    private ServerSocket server;
    private Socket socket;
    private DataInputStream dIn;
    private DataOutputStream dOut;
    private String clUser;
    private String svUser;
    private String lastMsg = " ";
    private int msg = 0;
    private int dist = 20;
    private boolean firstMsg = true;

    public void initialize() throws IOException {
        try {
            //INICIALIZA OS RECURSOS NECESSÁRIOS
            server = ConnectionFactory.getServer(); //INICIALIZA O SOCKET DO SERVIDOR
            System.out.println("FXML STARTED = Aguardando conexão...");
            System.out.println(server.getInetAddress() + " | " + server.getLocalPort());
            System.out.println(server.getLocalSocketAddress());
            socket = server.accept(); //ACEITA A CONEXÃO RECEBIDA
        } catch (IOException e) {
            e.printStackTrace();
        }
        dIn = new DataInputStream(socket.getInputStream());
        dOut = new DataOutputStream(socket.getOutputStream());
        Thread clientListener = new Thread(() -> {
            try {
                while (true) {
                    String clientMsg = dIn.readUTF(); //LÊ A MENSAGEM NO INPUT STREAM (CHARSET UTF)
                    if (!clientMsg.equals(lastMsg)) {
                        lastMsg = clientMsg;

                        //CASO O CLIENTE ENVIE /USERNAME
                        if (clientMsg.contains("/username")) {
                            String user[] = clientMsg.split(",");
                            clUser = user[1];

                            //CASO O CLIENTE ENVIE /QUIT
                        } else if (clientMsg.equals("/quit")) {
                            System.out.println("Conexão Encerrada.");
                            dIn.close();
                            dOut.close();
                            socket.close();
                            server.close();
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    Stage chat2 = (Stage) btnQuit.getScene().getWindow();
                                    chat2.close();
                                }
                            });
                            break;
                        } else {
                            System.out.println(clientMsg);
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    clAddLabel(clientMsg);//EXIBE A MENSAGEM NA TELA
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
    }

    public void setSvUser(String username) {
        svUser = username;
    }

    private void svAddLabel(String txt) {
        setSvUser(((Stage) lblBase.getScene().getWindow()).getTitle());
        Label lblMsg = new Label(svUser + ": " + txt);
        lblMsg.setStyle(lblBase.getStyle());
        msgBox.setPrefHeight(41 + (msg*dist));
        msgBox.getChildren().add(lblMsg);
        lblMsg.setLayoutX(14);
        lblMsg.setLayoutY((msg*dist) + 11);
        msg++;
    }

    private void clAddLabel(String txt) {
        setSvUser(((Stage) lblBase.getScene().getWindow()).getTitle());
        Label lblMsg = new Label(clUser + ": " + txt);
        lblMsg.setStyle(lblBase.getStyle());
        msgBox.setPrefHeight(41 + (msg*dist));
        msgBox.getChildren().add(lblMsg);
        lblMsg.setLayoutX(14);
        lblMsg.setLayoutY((msg*dist) + 11);
        msg++;
    }

    @FXML
    void closeConnection(ActionEvent event) throws IOException {
        String quit = "/quit";
        dOut.writeUTF(quit); //ESCREVE A MENSAGEM NO OUTPUT STREAM (CHARSET UTF)
        System.out.println("Conexão Encerrada.");
        dIn.close();
        dOut.close();
        socket.close();
        server.close();
        Stage chat = (Stage) btnQuit.getScene().getWindow();
        chat.close();
    }

    @FXML
    void sendMessage(ActionEvent event){
        try {
            if(firstMsg) {
                sendUser();
                firstMsg = false;
            }
            //SERVIDOR ENVIA A MENSAGEM
            String serverMsg = txtField.getText();
            dOut.writeUTF(serverMsg); //ESCREVE A MENSAGEM NO OUTPUT STREAM (CHARSET UTF)
            System.out.println(serverMsg);
            svAddLabel(serverMsg);    //EXIBE A MENSAGEM NA TELA
            txtField.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void sendUser() {
        try {
            //SERVIDOR ENVIA A MENSAGEM
            String serverMsg = ((Stage) lblBase.getScene().getWindow()).getTitle();
            dOut.writeUTF("/username," + serverMsg); //ESCREVE A MENSAGEM NO OUTPUT STREAM (CHARSET UTF)
            System.out.println("Username: " + serverMsg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}