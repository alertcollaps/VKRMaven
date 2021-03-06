package Server;

import Server.packet.OPacket;
import Server.packet.PacketManager;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
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
            if (!readData()){
                sleep();
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
    private boolean readData(){
        try{
            DataInputStream dis = new DataInputStream(client.getInputStream());
            BufferedReader br = new BufferedReader(new InputStreamReader(dis));
            while(true) {
                String s = br.readLine();
                if(s == null || s.trim().length() == 0) {
                    sleep();sleep();sleep();sleep();sleep();sleep();sleep();sleep();sleep();sleep();sleep();sleep();
                    continue;
                }
                System.out.println(s);
            }
            /*
            short id = dis.readShort();
            OPacket packet = PacketManager.getPacket(id);
            packet.setSocket(client);
            packet.read(dis);
            packet.handle();
            */

        }catch (IOException e){
            e.printStackTrace();
        }
        return true;
    }

    public void invalidate() {
        ServerLoader.invalidate(client);
    }
}
