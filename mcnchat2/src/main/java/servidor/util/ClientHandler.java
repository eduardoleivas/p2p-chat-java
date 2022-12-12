package main.java.servidor.util;

public class ClientHandler {

    private String username;
    private String ip;
    private ServerThread st;

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getIp() {
        return ip;
    }
    public void setIp(String ip) {
        this.ip = ip;
    }
    public ServerThread getSt() {
        return st;
    }
    public void setSt(ServerThread st) {
        this.st = st;
    }

    public ClientHandler(String username, String ip, ServerThread st) {
        this.username = username;
        this.ip = ip;
        this.st = st;
    }
}
