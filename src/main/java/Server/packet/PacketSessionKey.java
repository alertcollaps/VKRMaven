package Server.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PacketSessionKey extends OPacket{
    private String sessionKey;

    PacketSessionKey(String key){
        sessionKey = key;
    }

    @Override
    public short getId() {
        return 5;
    }

    @Override
    public void write(DataOutputStream dos) throws IOException {
        dos.writeUTF(sessionKey);
    }

    @Override
    public void read(DataInputStream dis) throws IOException {
        sessionKey = dis.readUTF();
    }

    @Override
    public void handle() {

    }
}
