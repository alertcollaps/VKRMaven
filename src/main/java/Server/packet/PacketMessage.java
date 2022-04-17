package Server.packet;

import Server.ClientHundler;
import Server.KeyManagerDH;
import Server.ServerLoader;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

public class PacketMessage extends OPacket {

    private String sender;
    private String message;

    public PacketMessage(){

    }

    public PacketMessage(String sender, String message){
        this.sender = sender;
        this.message = message;
    }

    @Override
    public short getId() {
        return 2;
    }

    @Override
    public void write(DataOutputStream dos) throws IOException {
        sender = ServerLoader.getHandler(getSocket()).getNickname();
        dos.writeUTF(sender);
        dos.writeUTF(message);
    }

    @Override
    public void read(DataInputStream dis) throws IOException {
        message = dis.readUTF();
    }

    @Override
    public void handle() {
        MessageArray.addMessage(ServerLoader.getHandler(getSocket()).getNickname() + ";" + message);
        ServerLoader.handlers.keySet().forEach(s -> ServerLoader.sendPacket(s, this));
    }

}
