package TestBC;

import Server.KeyManagerDH;

import javax.crypto.SecretKey;
import java.security.*;

public class Test {

    public static void main(String[] args) {

        System.out.println(new String(KeyManagerDH.getPair().getPublic().getEncoded()));
        System.out.println(new String(KeyManagerDH.getPair().getPublic().getEncoded()));
    }
}
