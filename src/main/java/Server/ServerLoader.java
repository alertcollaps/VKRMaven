package Server;


import Server.packet.OPacket;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.security.Key;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class ServerLoader {
    private static ServerSocket server;
    private static ServerHandler handler;
    public static ConcurrentHashMap<Socket, ClientHundler> handlers = new ConcurrentHashMap <>();
    private static Key key = KeyManagerDH.getSessionKeyAll();
    public static String DBurl;

    static {
        try {
            DBurl = "jdbc:sqlite:" + new java.io.File(".").getCanonicalPath() + "/JTP.db";
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        initializeDataBase();
        start();
        Logger log = Logger.getLogger(ServerLoader.class.getName());
        log.info("started");
        System.out.println("Start");
        handle();
    }
    private static void handle(){
        handler = new ServerHandler(server);
        handler.start();
        //readChat();
    }
//
    public static ServerHandler getServerHandler(){
        return handler;
    }

    private static void start(){
        try {
            System.out.println(System.getenv("PORT"));
            server = new ServerSocket(Integer.parseInt(System.getenv("PORT")));
            //server = new ServerSocket(8888);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void sleep(){
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void readChat() {
        Scanner scan = new Scanner(System.in);
        while (true){
            if (scan.hasNextLine()){
                String line = scan.nextLine();
                if (line == "/end"){
                    end();
                    return;
                } else {
                    System.out.println("Uncknown command");
                }


            }
        }
    }

    public static void end(){
        try {
            server.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.exit(0);
    }

    public static void sendPacket(Socket receiver, OPacket packet){
        try {
            DataOutputStream dos = new DataOutputStream(receiver.getOutputStream());
            dos.writeShort(packet.getId());
            packet.write(dos);
            dos.flush();
        } catch (IOException e) {
            if (e.getClass() == SocketException.class){
                try {
                    receiver.close();
                    ServerLoader.handlers.remove(receiver);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }

            e.printStackTrace();
        }
    }

    public static ClientHundler getHandler(Socket socket){
        return handlers.get(socket);
    }

    public static void invalidate(Socket socket){
        handlers.remove(socket);
    }

    public static Key getKey() {
        return key;
    }

    public static void initializeDataBase() {
        Connection conn = null;

        try {


            // create a connection to the database
            conn = DriverManager.getConnection(DBurl);

            Statement stmt = conn.createStatement();



            String sql = "CREATE TABLE IF NOT EXISTS COMPANY " +
                    "(EMAIL TEXT PRIMARY KEY     NOT NULL," +
                    " PASSWORD           TEXT    NOT NULL, " +
                    " NAME            TEXT     NOT NULL)";
            try {
                stmt.executeUpdate(sql);
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }

            sql = "CREATE TABLE IF NOT EXISTS MESSAGES " +
                    "(MESSAGE TEXT)";
            try {
                stmt.executeUpdate(sql);
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }

            stmt.close();
            System.out.println("Connection to SQLite has been established.");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }
    public static void addUser(String email, String password, String name){
        Connection conn = null;

        try {

            conn = DriverManager.getConnection(DBurl);

            PreparedStatement stmt = conn.prepareStatement
                    ("insert into COMPANY values(?,?,?)");

            stmt.setString(1, email);
            stmt.setString(2, password);
            stmt.setString(3, name);

            stmt.executeUpdate();
            stmt.close();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    public static int checkUser(String email, String password){
        Connection conn = null;

        try {

            conn = DriverManager.getConnection(DBurl);

            Statement stmt = conn.createStatement();


            String sql = "SELECT EXISTS(" +
                    "SELECT *" +
                    "FROM COMPANY " +
                    String.format("WHERE EMAIL='%s'", email) +
                    "LIMIT 1); ";
            ResultSet res = stmt.executeQuery(sql);
            int result1 = res.getInt(1);

            sql = "SELECT EXISTS(" +
                    "SELECT *" +
                    "FROM COMPANY " +
                    String.format("WHERE EMAIL='%s' AND PASSWORD = '%s'", email, password) +
                    "LIMIT 1); ";
            res = stmt.executeQuery(sql);
            int result2 = res.getInt(1);
            stmt.close();
            return result1 + result2;

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return 0;
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }

    }

}
