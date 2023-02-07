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

    //INITIALIZES THE NECESSARY RESOURCES
    public void initialize() throws IOException {
        try {
            server = ConnectionFactory.getServer(); //STARTS SERVER SOCKET
            socket = server.accept(); //ACCEPTS RECEIVED CONNECTION
        } catch (IOException e) {
            e.printStackTrace();
        }
        dIn = new DataInputStream(socket.getInputStream());
        dOut = new DataOutputStream(socket.getOutputStream());
        Thread clientListener = new Thread(() -> {
            try {
                while (true) {
                    String clientMsg = dIn.readUTF(); //READS INPUTSTREAM MESSAGES (UTF CHARSET)
                    if (!clientMsg.equals(lastMsg)) {
                        lastMsg = clientMsg;

                        //IF CLIENT SENDS /USERNAME
                        if (clientMsg.contains("/username")) {
                            String user[] = clientMsg.split(",");
                            clUser = user[1];

                        //IF CLIENT SENDS /QUIT
                        } else if (clientMsg.equals("/quit")) {
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
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    clAddLabel(clientMsg); //SHOWS THE MESSAGE ON THE CHAT BOX
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

    //METHOD SET SERVER USER
    public void setSvUser(String username) {
        svUser = username;
    }
    
    //METHOD SHOW SERVER MESSAGES
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

    //METHOD SHOW CLIENT MESSAGES
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

    @FXML //METHOD CLOSE CONNECTION
    void closeConnection(ActionEvent event) throws IOException {
        String quit = "/quit";
        dOut.writeUTF(quit); //WRITES MESSAGE ON OUTPUTSTREAM (UTF CHARSET)
        dIn.close();
        dOut.close();
        socket.close();
        server.close();
        Stage chat = (Stage) btnQuit.getScene().getWindow();
        chat.close();
    }

    @FXML //METHOD SEND MESSAGE
    void sendMessage(ActionEvent event){
        try {
            //IF IT'S THE FIRST MESSAGE
            if(firstMsg) {
                sendUser(); //SENDS USER DATA WITH IT
                firstMsg = false;
            }
            
            //CLIENT-SERVER SENDS THE MESSAGE
            String serverMsg = txtField.getText();
            dOut.writeUTF(serverMsg); //WRITES MESSAGE ON OUTPUTSTREAM (UTF CHARSET)
            svAddLabel(serverMsg);    //SHOWS THE MESSAGE ON THE CHAT BOX
            txtField.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //METHOD SEND USER
    void sendUser() {
        try {
            String serverMsg = ((Stage) lblBase.getScene().getWindow()).getTitle();
            dOut.writeUTF("/username," + serverMsg); //WRITES MESSAGE ON OUTPUTSTREAM (UTF CHARSET)
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
