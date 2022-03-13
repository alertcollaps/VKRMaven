package Server.packet;

import Server.ClientHundler;
import Server.KeyManagerDH;
import Server.ServerHandler;
import Server.ServerLoader;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.security.KeyPair;

public class PacketKeys extends OPacket {
    private String key;

    PacketKeys(){
        KeyPair pair = KeyManagerDH.getPair();
        try {
            key = KeyManagerDH.bytesToHex(KeyManagerDH.savePublicKey(pair.getPublic()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    PacketKeys(String key){
        this.key = key;
    }


    @Override
    public short getId() {
        return 3;
    }

    @Override
    public void write(DataOutputStream dos) throws IOException {
        dos.writeUTF(key);
    }

    @Override
    public void read(DataInputStream dis) throws IOException {
        key = dis.readUTF();
    }

    @Override
    public void handle() {
        byte[] keyPubClient = KeyManagerDH.hexToByteArray(key);
        KeyPair pair = KeyManagerDH.getPair();
        byte[] keyPrvThis = new byte[0];
        try {
            keyPrvThis = KeyManagerDH.savePrivateKey(pair.getPrivate());
            String sessionKey = KeyManagerDH.doECDH(keyPrvThis,keyPubClient);
            ServerLoader.getHandler(getSocket()).setSessionKey(sessionKey);
            ServerLoader.sendPacket(getSocket(), new PacketKeys(KeyManagerDH.bytesToHex(KeyManagerDH.savePublicKey(pair.getPublic()))));
            System.out.println("Session key: " + sessionKey);
            System.out.println("Pub key: " + key);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
