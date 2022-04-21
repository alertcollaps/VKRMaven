package Server.packet;

import Server.ServerLoader;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PacketLastMessages extends OPacket{

    @Override
    public short getId() {
        return 7;
    }

    @Override
    public void write(DataOutputStream dos) throws IOException {
        dos.writeUTF(MessageArray.getMessages());
    }

    @Override
    public void read(DataInputStream dis) throws IOException {

    }

    @Override
    public void handle() {
        ServerLoader.sendPacket(getSocket(), new PacketLastMessages());
    }
}
