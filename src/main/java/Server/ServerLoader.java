package Server;


import Server.packet.OPacket;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.Key;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class ServerLoader {
    private static ServerSocket server;
    private static ServerHandler handler;
    public static Map<Socket, ClientHundler> handlers = new HashMap<>();
    private static Key key = KeyManagerDH.getSessionKeyAll();

    public static void main(String[] args) {
        start();
        handle();
        end();
    }
    private static void handle(){
        handler = new ServerHandler(server);
        handler.start();
        readChat();
    }

    public static ServerHandler getServerHandler(){
        return handler;
    }

    private static void start(){
        try {
            server = new ServerSocket(8888);
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
}
