package main.java.util;

public class Usuario {

    private int id_user;
    private String username;
    private String password;

    //GETTERS AND SETTERS
    public int getId_user() {
        return id_user;
    }
    public void setId_user(int id_user) {
        this.id_user = id_user;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    //USER OBJECT CONSTRUCTOR (ID + USERNAME)
    public Usuario(int id_user, String username) {
        this.id_user = id_user;
        this.username = username;
    }

    //USER OBJECT CONSTRUCTOR (USERNAME + PASSWORD | OVERLOADED)
    public Usuario(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
