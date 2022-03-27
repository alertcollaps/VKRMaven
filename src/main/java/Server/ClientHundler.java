package Server;

import Server.packet.OPacket;
import Server.packet.PacketManager;
import Server.packet.PacketOK;

import java.io.*;
import java.net.Socket;

public class ClientHundler extends Thread {
    private final Socket client;
    private String nickname = "No client";
    private String SessionKey = "";

    public ClientHundler(Socket client){
        this.client = client;
    }

    public String getNickname(){
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setSessionKey(String sessionKey) {
        this.SessionKey = sessionKey;
    }
    public String getSessionKey(){
        return SessionKey;
    }

    @Override
    public void run() {
        while(true){



            try {
                DataOutputStream dos = new DataOutputStream(client.getOutputStream());
                PacketOK pk = new PacketOK();
                dos.writeShort(pk.getId());

                if (!readData()){
                    sleep();
                }
            } catch (IOException e) {
                System.out.println(e);
                System.out.println("Client disconnected");
                ServerLoader.handlers.remove(client);
                return;
            }


        }
    }

    private static void sleep(){
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    private boolean readData() throws IOException{

        DataInputStream dis = new DataInputStream(client.getInputStream());
        if (dis.available() <= 0){
            return false;
        }
        short id = dis.readShort();
        OPacket packet = PacketManager.getPacket(id);
        packet.setSocket(client);
        packet.read(dis);
        packet.handle();

        /*
        short id = dis.readShort();
        OPacket packet = PacketManager.getPacket(id);
        packet.setSocket(client);
        packet.read(dis);
        packet.handle();
        */



        return true;
    }

    public void invalidate() {
        ServerLoader.invalidate(client);
    }
}
