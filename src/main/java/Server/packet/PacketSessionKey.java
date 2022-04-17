package Server.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PacketSessionKey extends OPacket{
    private String sessionKey, error;

    PacketSessionKey(){

    }

    PacketSessionKey(String key, String error){
        sessionKey = key;
        this.error = error;
    }

    @Override
    public short getId() {
        return 5;
    }

    @Override
    public void write(DataOutputStream dos) throws IOException {
        dos.writeUTF(sessionKey);
        dos.writeUTF(error);
    }

    @Override
    public void read(DataInputStream dis) throws IOException {
        sessionKey = dis.readUTF();
        error = dis.readUTF();
    }

    @Override
    public void handle() {

    }
}
