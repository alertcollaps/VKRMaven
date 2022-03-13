package TestBC;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.*;

public class TestBC {

    public static void main(String[] args) {
        SecureRandom rand = new SecureRandom();
        KeyPairBC man1 = KeyPairBC.getKeys(rand);
        KeyPairBC man2 = KeyPairBC.getKeys(rand);
    }


}

class KeyPairBC{
    byte[] privateKey;
    byte[] publicKey;

    KeyPairBC(byte[] privateKey, byte[] publicKey){
        this.privateKey = privateKey;
        this.publicKey = publicKey;
    }

    public static KeyPairBC getKeys(SecureRandom rand){
        Provider provider = new BouncyCastleProvider();

        KeyPairGenerator keyPairGenerator  = null;
        try {
            keyPairGenerator = KeyPairGenerator.getInstance("DH");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        assert keyPairGenerator != null;


        keyPairGenerator.initialize(512, rand);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        byte[] privateKey = keyPair.getPrivate().getEncoded();
        byte[] publicKey = keyPair.getPublic().getEncoded();

        return new KeyPairBC(privateKey, publicKey);
    }
}

