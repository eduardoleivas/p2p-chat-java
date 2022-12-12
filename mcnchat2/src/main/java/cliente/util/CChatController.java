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
import java.net.Socket;

public class CChatController {

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

    private Socket client;
    private DataInputStream dIn;
    private DataOutputStream dOut;
    private String clUser;
    private String svUser;
    private String lastMsg = " ";
    private int msg = 0;
    private int dist = 20;
    private boolean firstMsg = true;

    public void initialize() throws IOException {
        //INICIALIZA OS RECURSOS NECESSÁRIOS
        client = ConnectionFactory.getChat(); //INICIALIZA O SOCKET
        System.out.println("FXML STARTED = Conectado com sucesso!");
        System.out.println(client.getInetAddress() + " | " + client.getPort());
        System.out.println(client.getRemoteSocketAddress() + " | " + client.getLocalSocketAddress());
        dIn = new DataInputStream(client.getInputStream());
        dOut = new DataOutputStream(client.getOutputStream());
        Thread serverListener = new Thread(() -> {
            try {
                while(true) {
                    //RECEBE A MENSAGEM DO SERVIDOR
                    String serverMsg = dIn.readUTF(); //LÊ A MENSAGEM DO INPUTSTREAM (CHARSET UTF)
                    if (!serverMsg.equals(lastMsg)) {
                        lastMsg = serverMsg;
                        //CASO O SERVIDOR ENVIE /QUIT

                        if (serverMsg.contains("/username")) {
                            String user[] = serverMsg.split(",");
                            svUser = user[1];

                            //CASO O SERVIDOR ENVIE /QUIT
                        } else if (serverMsg.equals("/quit")) {
                            System.out.println("Conexão Encerrada.");
                            dIn.close();
                            dOut.close();
                            client.close();
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    Stage chat2 = (Stage) btnQuit.getScene().getWindow();
                                    chat2.close();
                                }
                            });
                            break;
                        } else {
                            System.out.println("Server: " + serverMsg);
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    svAddLabel(serverMsg); //EXIBE A MENSAGEM NA TELA
                                }
                            });
                        }
                    }
                }
            } catch (IOException e) {
                //e.printStackTrace();
            }
        });
        serverListener.start();
    }

    public void setClUser(String username) {
        clUser = username;
    }

    private void svAddLabel(String txt) {
        setClUser(((Stage) lblBase.getScene().getWindow()).getTitle());
        Label lblMsg = new Label(svUser + ": " + txt);
        lblMsg.setStyle(lblBase.getStyle());
        msgBox.setPrefHeight(41 + (msg*dist));
        msgBox.getChildren().add(lblMsg);
        lblMsg.setLayoutX(14);
        lblMsg.setLayoutY((msg*dist) + 11);
        msg++;
    }

    private void clAddLabel(String txt) {
        setClUser(((Stage) lblBase.getScene().getWindow()).getTitle());
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
        dOut.writeUTF(quit); //ESCREVE MENSAGEM NO OUTPUTSTREAM (CHARSET UTF)
        System.out.println("Conexão Encerrada.");
        dIn.close();
        dOut.close();
        client.close();
        Stage chat = (Stage) btnQuit.getScene().getWindow();
        chat.close();
    }

    @FXML
    void sendMessage(ActionEvent event) {
        try {
            if(firstMsg) {
                sendUser();
                firstMsg = false;
            }
            //CLIENTE ENVIA A MENSAGEM
            String clientMsg = txtField.getText();
            dOut.writeUTF(clientMsg); //ESCREVE MENSAGEM NO OUTPUTSTREAM (CHARSET UTF)
            System.out.println(clientMsg);
            clAddLabel(clientMsg);
            txtField.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void sendUser() {
        try {
            //SERVIDOR ENVIA A MENSAGEM
            String clientMsg = ((Stage) lblBase.getScene().getWindow()).getTitle();
            dOut.writeUTF("/username," + clientMsg); //ESCREVE A MENSAGEM NO OUTPUT STREAM (CHARSET UTF)
            System.out.println("Username: " + clientMsg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
