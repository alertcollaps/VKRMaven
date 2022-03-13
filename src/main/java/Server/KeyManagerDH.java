package Server;

import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.interfaces.ECPrivateKey;
import org.bouncycastle.jce.interfaces.ECPublicKey;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.provider.BrokenJCEBlockCipher;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.bouncycastle.jce.spec.ECPrivateKeySpec;
import org.bouncycastle.jce.spec.ECPublicKeySpec;
import org.bouncycastle.pqc.jcajce.provider.BouncyCastlePQCProvider;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.util.Base64;

public class KeyManagerDH {
    final protected static char[] hexArray = "0123456789abcdef".toCharArray();
    public static KeyPair pair = generateKeyPair();
    private static Key sessionKeyAll = generateSessionKey();
    private static KeyGenerator kg;
    private static Cipher encrypt;

    public static KeyPair getPair(){
        return pair;
    }

    public static Key getSessionKeyAll() {
        return sessionKeyAll;
    }

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static byte[] hexToByteArray(String hexString) {
        int len = hexString.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4 )+Character.digit(hexString.charAt(i + 1), 16));
        }
        return data;
    }

    public static byte [] savePublicKey (PublicKey key) throws Exception
    {
        //return key.getEncoded();

        ECPublicKey eckey = (ECPublicKey)key;
        return eckey.getQ().getEncoded(true);
    }

    public static PublicKey loadPublicKey (byte [] data) throws Exception
    {
		/*KeyFactory kf = KeyFactory.getInstance("ECDH", "BC");
		return kf.generatePublic(new X509EncodedKeySpec(data));*/

        ECParameterSpec params = ECNamedCurveTable.getParameterSpec("prime192v1");
        ECPublicKeySpec pubKey = new ECPublicKeySpec(
                params.getCurve().decodePoint(data), params);
        KeyFactory kf = KeyFactory.getInstance("ECDH", "BC");
        return kf.generatePublic(pubKey);
    }

    public static byte [] savePrivateKey (PrivateKey key) throws Exception
    {
        //return key.getEncoded();

        ECPrivateKey eckey = (ECPrivateKey)key;
        return eckey.getD().toByteArray();
    }

    public static PrivateKey loadPrivateKey (byte [] data) throws Exception
    {
        //KeyFactory kf = KeyFactory.getInstance("ECDH", "BC");
        //return kf.generatePrivate(new PKCS8EncodedKeySpec(data));

        ECParameterSpec params = ECNamedCurveTable.getParameterSpec("prime192v1");
        ECPrivateKeySpec prvkey = new ECPrivateKeySpec(new BigInteger(data), params);
        KeyFactory kf = KeyFactory.getInstance("ECDH", "BC");
        return kf.generatePrivate(prvkey);
    }

    public static String doECDH (byte[] dataPrv, byte[] dataPub) throws Exception
    {
        KeyAgreement ka = KeyAgreement.getInstance("ECDH", "BC");
        ka.init(loadPrivateKey(dataPrv));
        ka.doPhase(loadPublicKey(dataPub), true);
        byte [] secret = ka.generateSecret();
        return bytesToHex(secret);
    }
    public static KeyPair generateKeyPair() {
        Security.addProvider(new BouncyCastleProvider());

        KeyPairGenerator kpgen = null;
        try {
            kpgen = KeyPairGenerator.getInstance("ECDH", "BC");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }
        try {
            kpgen.initialize(new ECGenParameterSpec("prime192v1"), new SecureRandom());
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
        KeyPair pair = kpgen.generateKeyPair();
        return pair;
    }
    public static Key generateSessionKey(){
        Provider bc = new BouncyCastleProvider();
        Security.addProvider(bc);
        try {
            encrypt = Cipher.getInstance("GOST3412-2015/CBC/PKCS5PADDING", bc);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }
        try {
            kg = KeyGenerator.getInstance("GOST3412-2015", bc);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        kg.init(new SecureRandom());
        Key key = kg.generateKey();
        return key;
    }
    public static String encryptGost(String data, Key sessionKey){
        Provider bc = new BouncyCastleProvider();

        Security.addProvider(bc);
        byte[] initByte = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};

        try {
            encrypt.init(Cipher.ENCRYPT_MODE, sessionKey, new IvParameterSpec(initByte));
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
        byte[] enc = new byte[0];
        try {
            enc = encrypt.doFinal(data.getBytes(StandardCharsets.UTF_8));
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        return bytesToHex(enc);
    }
    public static String decryptGost(String data, Key sessionKey){
        Provider bc = new BouncyCastleProvider();
        Security.addProvider(bc);

        byte[] enc = hexToByteArray(data);

        byte[] initByte = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};

        try {
            encrypt.init(Cipher.DECRYPT_MODE, sessionKey, new IvParameterSpec(initByte));
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
        byte[] dec = new byte[0];
        try {
            dec = encrypt.doFinal(enc);
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        return new String(dec);
    }
    public static Key StringToKey(String keyStr){
        Provider BCprovider = new BouncyCastleProvider();
        Security.addProvider(BCprovider);


        byte[] kk = Base64.getDecoder().decode(keyStr);
        SecretKey key = new SecretKeySpec(kk, 0, kg.generateKey().getEncoded().length, "GOST3412-2015");
        return key;
    }
    public static String KeyToString(Key key){
        String encodedKey = Base64.getEncoder().encodeToString(key.getEncoded());
        return encodedKey;
    }

}
