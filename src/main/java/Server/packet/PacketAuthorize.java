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
    private String nickname, email, password;

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
        email = dis.readUTF();
        password = dis.readUTF();

        String keyStr = ServerLoader.getHandler(getSocket()).getSessionKey();
        Key key = KeyManagerDH.StringToKey(keyStr);

        nickname = KeyManagerDH.decryptGost(nickname, key);
        email = KeyManagerDH.decryptGost(email, key);
        password = KeyManagerDH.decryptGost(password, key);


        System.out.println("Nickname " + nickname);

    }

    @Override
    public void handle(){
        String error = "";
        String keyAll = "";
        String key = ServerLoader.getHandler(getSocket()).getSessionKey();
        int checkUs = ServerLoader.checkUser(email, password);
        switch (checkUs){
            case 0:
                ServerLoader.addUser(email, password, nickname);
                error = "NONE";

                nickname = KeyManagerDH.encryptGost(nickname, ServerLoader.getKey());;
                ServerLoader.getHandler(getSocket()).setNickname(nickname);

                keyAll = KeyManagerDH.encryptGost(KeyManagerDH.KeyToString(ServerLoader.getKey()), KeyManagerDH.StringToKey(key));
                System.out.println("SessionAll:" + KeyManagerDH.KeyToString(ServerLoader.getKey()));
                break;
            case 1:
                keyAll = "";
                error = "Invalid password";
                break;
            case 2:
                error = "NONE";
                nickname = KeyManagerDH.encryptGost(nickname, ServerLoader.getKey());;
                ServerLoader.getHandler(getSocket()).setNickname(nickname);

                keyAll = KeyManagerDH.encryptGost(KeyManagerDH.KeyToString(ServerLoader.getKey()), KeyManagerDH.StringToKey(key));
                System.out.println("SessionAll:" + KeyManagerDH.KeyToString(ServerLoader.getKey()));
                break;
            default:

        }

        ServerLoader.sendPacket(getSocket(), new PacketSessionKey(keyAll, error));
    }
}
