package com.agent.fasttag.encript;



import android.content.Context;
import android.content.res.AssetManager;
import android.os.Build;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
public class TestEncryption {
     Context mContext;
    public TestEncryption(Context context){
        this.mContext=context;
    }

    String userDir =System.getProperty("user.dir");



    private String m2pPublicFile =  userDir+File.separator+"keys"+File.separator+"ashwitha.pubkey";
    private String busPrivateFile = userDir+File.separator+"keys"+File.separator+"ashwitha.pkcs8";

    private String symmetricKeyAlgorithm = "AES/CBC/PKCS5Padding";

    private String asymmetricKeyAlgorithm = "RSA/ECB/PKCS1Padding";

    private String digitalSignatureAlgorithm = "SHA1withRSA";

    private String pkiProvider = "BC";



    public String encryptData(String requestData, byte[] sessionKey, String messageRefNo) throws Exception {

        SecretKey secKey = new SecretKeySpec(sessionKey, "AES");
        Cipher cipher = Cipher.getInstance(symmetricKeyAlgorithm);
        IvParameterSpec ivSpec = new IvParameterSpec(messageRefNo.getBytes());
        cipher.init(Cipher.ENCRYPT_MODE, secKey, ivSpec);
        byte[] newData = cipher.doFinal(requestData.getBytes());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return Base64.getEncoder().encodeToString(newData);
        }
        return requestData;
    }

    public String encryptKey(byte[] sessionKey) throws Exception {

        PublicKey pubKey = readPublicKeyFromFile(m2pPublicFile);
        System.out.println("Generated ::  pubKey::"+pubKey);
        Cipher cipher = Cipher.getInstance(asymmetricKeyAlgorithm);

        cipher.init(Cipher.ENCRYPT_MODE, pubKey);
        byte[] cipherData = cipher.doFinal(sessionKey);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return Base64.getEncoder().encodeToString(cipherData);
        }

        return null;
    }

    public byte[] generateToken() throws Exception {
        KeyGenerator generator = KeyGenerator.getInstance("AES");
        generator.init(128);
        SecretKey key = generator.generateKey();

        System.out.println("Key value" +  key.getEncoded());
        String testkey = "1234123412341234";
        byte[] symmetricKey = testkey.getBytes();
        return key.getEncoded();
    }

    public String generateDigitalSignedToken(String requestData) throws Exception {

        // Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

        Signature signature = Signature.getInstance(digitalSignatureAlgorithm);
        PrivateKey privateKey = this.readPrivateKeyFromFile(busPrivateFile);

        System.out.println(" Generated :: privateKey::"+privateKey);

        signature.initSign(privateKey, new SecureRandom());

        byte[] message = requestData.getBytes();
        signature.update(message);

        byte[] sigBytes = signature.sign();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return Base64.getEncoder().encodeToString(sigBytes);
        }

        return requestData;
    }

    private PrivateKey readPrivateKeyFromFile(String keyFileName) throws Exception {
     /*   File filePrivateKey = new File(keyFileName);
        FileInputStream fis = new FileInputStream(filePrivateKey);*/

       AssetManager am = mContext.getAssets();
      /*   String[] files = am.list("Files");
        for(int i=0; i<files.length; i++)            {
            System.out.println("files:: "+files[i]);

        }
        File filePrivateKey = new File(files[0]);*/

        InputStream fis = am.open("ashwitha.pkcs8");
//        FileInputStream fis= (FileInputStream) am.open("ashwitha.pkcs8");
        System.out.println("InputStream:: "+fis);
        int size = fis.available();

        byte[] encodedPrivateKey = new byte[(int)size];
        fis.read(encodedPrivateKey);
        fis.close();
        String tContents = new String(encodedPrivateKey);
        System.out.println("InputStream:: "+tContents);

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(encodedPrivateKey);
        PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);
        return privateKey;
    }

    private PublicKey readPublicKeyFromFile(String keyFileName) throws Exception {

//        File filePublicKey = new File(keyFileName);
//        FileInputStream fis = new FileInputStream(filePublicKey);

//        new File("//android_asset/luc.jpeg");
        AssetManager am = mContext.getAssets();
//        String[] files = am.list("Files");
//        System.out.println("files:: "+files);
//        File filePublicKey = new File(files[0]);

        InputStream fis = am.open("m2psolutions_pub.cer");
        int size = fis.available();
        BufferedReader br = new BufferedReader(new InputStreamReader(fis, "UTF-8"));

        byte[] encodedPublicKey = new byte[(int) size];
        fis.read(encodedPublicKey);
        fis.close();

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(encodedPublicKey);
        PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);
        return publicKey;
    }

    public String decodeResponse(Map responseMap) throws Exception {
        String messageRefNo = responseMap.get("refNo").toString();
        String sessionKey = responseMap.get("key").toString();
        String token = responseMap.get("hash").toString();
        String body = responseMap.get("body").toString();
        return this.decryptMessage(body,
                sessionKey, token, messageRefNo);
    }

    public String decryptMessage(String xmlResponse, String encSessionKey, String token, String messageRefNo) throws Exception {

        byte[] sessionKey = this.decryptSessionKey(encSessionKey);
        String data = decryptWithAESKey(xmlResponse, sessionKey, messageRefNo.getBytes());
        return data;

    }

    private String decryptWithAESKey(String inputData, byte[] key, byte[] iv)
            throws Exception {

        Cipher cipher = Cipher.getInstance(symmetricKeyAlgorithm);
        SecretKeySpec secKey = new SecretKeySpec(key, symmetricKeyAlgorithm);

        IvParameterSpec spec = new IvParameterSpec(iv);

        cipher.init(Cipher.DECRYPT_MODE, secKey, spec);
        byte[] newData = new byte[0];
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            newData = cipher.doFinal(Base64.getDecoder().decode(inputData));
        }
        return new String(newData);

    }

    private byte[] decryptSessionKey(String sessionKey) throws Exception {

        PrivateKey privateKey = readPrivateKeyFromFile(busPrivateFile);
        Cipher cipher = Cipher.getInstance(asymmetricKeyAlgorithm);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            cipher.update(Base64.getDecoder().decode(sessionKey));
        }
        byte[] sessionKeyBytes = cipher.doFinal();
        return sessionKeyBytes;

    }

    public String encodeRequest(String requestData, String messageRefNo, String entity) throws Exception {
        byte[] sessionKeyByte = this.generateToken();
        String data = " token:" + this.generateDigitalSignedToken(requestData) + ", body: "
                + this.encryptData(requestData, sessionKeyByte, messageRefNo) + ", entity:"
                + this.encryptKey(entity.getBytes()) + ",key: " + this.encryptKey(sessionKeyByte) + ", refNo:"
                + messageRefNo;
        System.out.println("encodeRequest::"+data);
        return data;

    }

   /* public static void main(String[] args) {

        String requestData = "{\"entityId\": \"s3QhsIHWErcmnzkruALBtqeAjAu1\"}";
        TestEncryption enc = new TestEncryption();
        try
        {
            enc.encodeRequest(requestData, "1234123412341238", "FINOWERIZE");
            Map<String, String> responseMap = new HashMap<>();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }*/


}

