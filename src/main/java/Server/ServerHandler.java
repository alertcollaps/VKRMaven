package Server;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class ServerHandler extends Thread{

    private final ServerSocket server;

    ServerHandler(ServerSocket server){
        this.server = server;
    }

    @Override
    public void run() {
        while(true){
            Socket client;
            try {
                client = server.accept();
                System.out.println("request" + client);
                clientHandle(client);
            } catch (SocketException ex){
                return;
            } catch (IOException e) {
                e.printStackTrace();
            }
            ServerLoader.sleep();
        }
    }

    public void clientHandle(Socket client){
        Thread handleKey = new Thread(){
            @Override
            public void run(){
                ClientHundler handler = new ClientHundler(client);
                handler.start();
                ServerLoader.handlers.put(client, handler);
            }
        };
        handleKey.start();

    }
}
