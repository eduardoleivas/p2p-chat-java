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
    
    //INITIALIZES NECESSARY RESOURCES
    public void initialize() throws IOException {
        client = ConnectionFactory.getChat(); //SOCKET INITIALIZATION
        dIn = new DataInputStream(client.getInputStream());
        dOut = new DataOutputStream(client.getOutputStream());
        Thread serverListener = new Thread(() -> {
            try {
                while(true) {
                    //RECEIVES A MESSAGE FROM CONNECTED
                    String serverMsg = dIn.readUTF(); //LÊ A MENSAGEM DO INPUTSTREAM (CHARSET UTF)
                    if (!serverMsg.equals(lastMsg)) {
                        lastMsg = serverMsg;
                        
                        //IF CONNECTED SENDS USER DATA
                        if (serverMsg.contains("/username")) {
                            String user[] = serverMsg.split(",");
                            svUser = user[1];

                        //IF CONNECTED SENDS /QUIT
                        } else if (serverMsg.equals("/quit")) {
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
                e.printStackTrace();
            }
        });
        serverListener.start();
    }

    public void setClUser(String username) {
        clUser = username;
    }

    //SHOWS RECEIVED MESSAGE ON THE CHAT BOX
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

    //SHOWS THE SENT MESSAGE ON THE CHAT BOX
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

    @FXML //CLOSE CONNECTION METHOD
    void closeConnection(ActionEvent event) throws IOException {
        String quit = "/quit";
        dOut.writeUTF(quit); //ESCREVE MENSAGEM NO OUTPUTSTREAM (CHARSET UTF)
        dIn.close();
        dOut.close();
        client.close();
        Stage chat = (Stage) btnQuit.getScene().getWindow();
        chat.close();
    }

    @FXML //SEND MESSAGE METHO
    void sendMessage(ActionEvent event) {
        try {
            if(firstMsg) {
                sendUser();
                firstMsg = false;
            }
            //CLIENT SENDS THE MESSAGE
            String clientMsg = txtField.getText();
            dOut.writeUTF(clientMsg); //WRITES MESSAGE ON OUTPUTSTREAM (UTF CHARSET)
            clAddLabel(clientMsg);
            txtField.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //SENDS USER INFO
    void sendUser() {
        try {
            //SERVER SENDS THE MESSAGE
            String clientMsg = ((Stage) lblBase.getScene().getWindow()).getTitle();
            dOut.writeUTF("/username," + clientMsg); //WRITES MESSAGE ON OUTPUTSTREAM (UTF CHARSET)
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
