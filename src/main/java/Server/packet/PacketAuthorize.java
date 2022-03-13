package Server.packet;

import Server.KeyManagerDH;
import Server.ServerLoader;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.security.Key;
import java.util.Base64;

public class PacketAuthorize extends OPacket {
    private String nickname;

    public PacketAuthorize(){

    }

    public PacketAuthorize(String nickname){
        this.nickname = nickname;
    }

    @Override
    public short getId() {
        return 1;
    }

    @Override
    public void write(DataOutputStream dos) throws IOException {

    }

    @Override
    public void read(DataInputStream dis) throws IOException {
        nickname = dis.readUTF();
        String keyStr = ServerLoader.getHandler(getSocket()).getSessionKey();
        nickname = KeyManagerDH.decryptGost(nickname, KeyManagerDH.StringToKey(keyStr));
        System.out.println("Nickname " + nickname);
    }

    @Override
    public void handle(){
        ServerLoader.getHandler(getSocket()).setNickname(nickname);
        String key = ServerLoader.getHandler(getSocket()).getSessionKey();
        String keyAll = KeyManagerDH.encryptGost(KeyManagerDH.KeyToString(ServerLoader.getKey()), KeyManagerDH.StringToKey(key));
        System.out.println("SessionAll:" + KeyManagerDH.KeyToString(ServerLoader.getKey()));
        ServerLoader.sendPacket(getSocket(), new PacketSessionKey(keyAll));
    }
}
