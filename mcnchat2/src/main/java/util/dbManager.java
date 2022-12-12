package main.java.util;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class dbManager {

    //CONECTA COM O BD
    public static Connection dbConnect(){
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = null;
            conn = DriverManager.getConnection("jdbc:mysql://localhost/redes","root", "");
            System.out.print("Database is connected !");
            return conn;
        }
        catch(SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    //CRIA TUPLA NO BD
    public static void dbCreate(Connection conn, String table, String param1, String param2, String value1, String value2) throws SQLException {
        String sql = "INSERT INTO "+ table +" (" +param1+ ", " +param2+ ") VALUES (?, ?)";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        try {
            try {
                pstmt.setString(1, value1); //VALUE 1
                pstmt.setString(2, value2); //VALUE 2
                pstmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } finally {
            pstmt.close();
        }
    }

    //LÊ TUPLA NO BD
    public static ResultSet dbReadString(Connection conn, String select, String table, String param1, String value1) throws SQLException{
        String sql = "SELECT "+ select +" FROM "+ table +" WHERE "+ param1 +"= '"+ value1+"'";
        System.out.println(sql);
        Statement stmt = conn.createStatement();
        try {
            ResultSet rs = stmt.executeQuery(sql);
            return rs;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            stmt.close();
        }
        return null;
    }

    //RECUPERA O ID DO USUARIO PELO USERNAME
    public static int dbGetIdByUsername(Connection conn, String select, String table, String param1, String value1) throws SQLException{
        String sql = "SELECT "+ select +" FROM "+ table +" WHERE "+ param1 +"= '"+ value1+"'";
        System.out.println(sql);
        Statement stmt = conn.createStatement();
        try {
            ResultSet rs = stmt.executeQuery(sql);
            rs.next();
            return rs.getInt(select);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            stmt.close();
        }
        return 0;
    }

    //RECUPERA O USERNAME DO USUARIO PELO ID
    public static String dbGetUsernameById(Connection conn, String select, String table, String param1, int value1) throws SQLException{
        String sql = "SELECT "+ select +" FROM "+ table +" WHERE "+ param1 +"= "+ value1;
        System.out.println(sql);
        Statement stmt = conn.createStatement();
        try {
            ResultSet rs = stmt.executeQuery(sql);
            rs.next();
            String username = rs.getString("username");
            return username;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            stmt.close();
        }
        return null;
    }

    //RECUPERA A LISTA DE AMIGOS DO USUARIO PELO SEU ID
    public static List<Usuario> dbGetFriends(Connection conn, String select, String table, String param1, int value1) throws SQLException{
        String sql = "SELECT "+ select +" FROM "+ table +" WHERE "+ param1 +"= "+ value1;
        System.out.println(sql);
        Statement stmt = conn.createStatement();
        try {
            ResultSet rs = stmt.executeQuery(sql);
            List<Usuario> amigos = new ArrayList<>();
            while(rs.next()) {
                int temp_id = rs.getInt("id_amigo");
                String temp_user = dbGetUsernameById(conn, "username", "usuario", "id_user", temp_id);
                Usuario user = new Usuario(temp_id, temp_user);
                amigos.add(user);
            }
            return amigos;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            stmt.close();
        }
        return null;
    }

    //RECUPERA A LISTA DE AMIGOS DO USUARIO PELO SEU ID
    public static List<Usuario> dbGetOnlineFriends(Connection conn, int value1) throws SQLException{
        String sql = "SELECT id_amigo FROM contatos c, usuario u WHERE c.id_amigo = u.id_user AND u.status=1 AND c.id_user = "+ value1;
        System.out.println(sql);
        Statement stmt = conn.createStatement();
        try {
            ResultSet rs = stmt.executeQuery(sql);
            List<Usuario> amigos = new ArrayList<>();
            while(rs.next()) {
                int temp_id = rs.getInt("id_amigo");
                String temp_user = dbGetUsernameById(conn, "username", "usuario", "id_user", temp_id);
                Usuario user = new Usuario(temp_id, temp_user);
                amigos.add(user);
            }
            return amigos;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            stmt.close();
        }
        return null;
    }

    public static ResultSet dbReadInt(Connection conn, String select, String table, String param1, String value1) throws SQLException{
        String sql = "SELECT "+ select +" FROM "+ table +" WHERE "+ param1 +"= "+ value1;
        System.out.println(sql);
        Statement stmt = conn.createStatement();
        try {
            ResultSet rs = stmt.executeQuery(sql);
            return rs;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    //ATUALIZA TUPLA NO BD
    public static void dbUpdate(Connection conn, String table, String param1, String param2, String value1, String value2, int value3) throws SQLException {
        String sql = "UPDATE "+ table +" SET "+ param1 +"= ?, "+ param2 + "= ? WHERE id_user= ?";
        System.out.println(sql);
        PreparedStatement pstmt = conn.prepareStatement(sql);
        try {
            try {
                pstmt.setString(1, value1); //VALUE 1
                pstmt.setString(2, value2); //VALUE 2
                pstmt.setInt(3, value3); //VALUE 3
                pstmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } finally {
            pstmt.close();
        }
    }

    //ATUALIZA TUPLA NO BD
    public static void dbStatusUpdate(Connection conn, int status, String username) throws SQLException {
        String sql = "UPDATE usuario SET  status= ? WHERE username= ?";
        System.out.println(sql);
        PreparedStatement pstmt = conn.prepareStatement(sql);
        try {
            try {
                pstmt.setInt(1, status); //VALUE 1
                pstmt.setString(2, username); //ID
                pstmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } finally {
            pstmt.close();
        }
    }

    //DELETA TUPLA NO BD
    public static void dbDelete(Connection conn, String table, String param1, int value1) throws SQLException{
        String sql = "DELETE FROM "+ table + " WHERE "+ param1 +"= ?";
        System.out.println(sql);
        PreparedStatement pstmt = conn.prepareStatement(sql);
        try {
            try {
                pstmt.setInt(1, value1); //VALUE 1
                pstmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } finally {
            pstmt.close();
        }
    }

    public static Boolean dbLogin(Connection conn, String username, String pass) throws SQLException{
        String sql = "SELECT  COUNT(*) as loginCheck FROM usuario WHERE username='"+ username +"' AND  pass='"+ pass+"'";
        System.out.println(sql);
        Statement stmt = conn.createStatement();
        try {
            ResultSet rs = stmt.executeQuery(sql);
            rs.next();
            if (rs.getInt(1) == 1) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    //FECHA A CONEXÃO COM O DB
    public static void dbClose(Connection conn) {
        try {
            if(!conn.isClosed()) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
