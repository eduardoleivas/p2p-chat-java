package main.java.cliente.util;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import main.java.util.Usuario;

import java.io.IOException;

public class ClientController {
    @javafx.fxml.FXML
    private AnchorPane ap;
    @javafx.fxml.FXML
    private TextField txtUsername;
    @javafx.fxml.FXML
    private TextField txtSenha;
    @javafx.fxml.FXML
    private Button btnConnect;
    @javafx.fxml.FXML
    private TextField txtHost;
    @javafx.fxml.FXML
    private TextField txtPort;

    private static Usuario user;

    @javafx.fxml.FXML //CONNECTS CLIENT TO SERVER
    public void clientConnect(ActionEvent actionEvent) {
        String host = txtHost.getText();
        int port = Integer.parseInt(txtPort.getText());
        ConnectionFactory.connectionFactory(host, port);
        user = new Usuario(txtUsername.getText(), txtSenha.getText());
        startChat();
    }

    //INITIALIZES THE FRIEND LIST AFTER USER LOGIN
    private void startChat() {
        try {
            Stage clChat = new Stage();
            Parent root = FXMLLoader.load(getClass().getResource("/cliente/friendList.fxml"));
            Scene scene = new Scene(root);
            clChat.setTitle("Lista de Amigos");
            clChat.setResizable(false);
            clChat.setScene(scene);
            clChat.show();
            clChat.setOnCloseRequest(event-> {
                System.exit(0);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //METHOD TO RETURN USER INFO
    public static Usuario getUser() {
        return user;
    }
}
