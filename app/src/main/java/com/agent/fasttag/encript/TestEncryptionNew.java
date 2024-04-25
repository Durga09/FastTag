package com.agent.fasttag.encript;

import android.content.Context;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Build;

import com.agent.fasttag.R;
import com.google.gson.JsonObject;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URI;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.io.FileReader;
import java.io.BufferedReader;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.util.Random;

public class TestEncryptionNew {
    Context mContext;
    public TestEncryptionNew(Context context){
        this.mContext=context;
    }

    String userDir =System.getProperty("user.dir");

//    private String m2pPublicFile =  userDir+File.separator+"keys"+File.separator+"m2psolutions_pub.cer";
//    private String busPrivateFile = userDir+File.separator+"keys"+File.separator+"ashwitha.pkcs8";
    private String m2pPublicFile =  "file:///android_asset/m2psolutions_pub.cer";
    private String busPrivateFile = "file:///android_asset/ashwitha.pkcs8";
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
//        String testkey = "1234123412341234";
//        byte[] symmetricKey = testkey.getBytes();
        return key.getEncoded();
    }

    public String generateDigitalSignedToken(String requestData) throws Exception {

        // Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

        Signature signature = Signature.getInstance(digitalSignatureAlgorithm);
        PrivateKey privateKey = this.readPrivateKeyFromFile(busPrivateFile);
        System.out.println(privateKey);
        signature.initSign(privateKey, new SecureRandom());

        byte[] message = requestData.getBytes();
        signature.update(message);

        byte[] sigBytes = signature.sign();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return Base64.getEncoder().encodeToString(sigBytes);
        }

        return requestData;
    }

    public PrivateKey readPrivateKeyFromFile(String keyFileName) throws Exception {
//        File filePrivateKey = new File(keyFileName);
//        FileInputStream fis = new FileInputStream(filePrivateKey);
        AssetManager am = mContext.getAssets();
        AssetManager.AssetInputStream fis = (AssetManager.AssetInputStream) am.open("ashwitha.pkcs8");
        int filePrivateKeyLength = fis.available();
      /* try {
            String[] files = am.list("");

            for(int i=0; i<files.length; i++)            {
                System.out.println("Assets Files :"+i+" Name => "+files[i]);
                if(files[i].equals("ashwitha.pkcs8")){
                    File filePrivateKey1 = new File("/data/user/0/com.agent.fasttag/files/ashwitha.pkcs84377672001022710791");
                    FileInputStream fis1 = new FileInputStream(filePrivateKey1);

                    System.out.println("FileInputStream:: "+fis1);

                    byte[] encodedPrivateKey = new byte[(int) filePrivateKey1.length()];
                    fis1.read(encodedPrivateKey);
                    fis1.close();
                    KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                    PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(encodedPrivateKey);
                    PrivateKey privateKey1 = keyFactory.generatePrivate(privateKeySpec);

                    System.out.println("FileInputStream loop privateKey :: "+privateKey1);

                }
            }
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }*/
        System.out.println("readPrivateKeyFromFile fis::"+filePrivateKeyLength+"   "+fis);
        byte[] encodedPrivateKey = new byte[(int) filePrivateKeyLength];
        fis.read(encodedPrivateKey);
        fis.close();
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(encodedPrivateKey);
        PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);
        System.out.println("FileInputStream privateKey :: "+privateKey);

        return privateKey;
    }

    //    private PublicKey readPublicKeyFromFile(String keyFileName) throws Exception {
//
//        File filePublicKey = new File(keyFileName);
//        FileInputStream fis = new FileInputStream(filePublicKey);
//        byte[] encodedPublicKey = new byte[(int) filePublicKey.length()];
//
//        fis.read(encodedPublicKey);
//        fis.close();
//
//        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
//        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(encodedPublicKey);
//        PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);
//        return publicKey;
//
//    }
    private  PublicKey readPublicKeyFromFile(String keyFileName) throws Exception {
        BufferedReader reader = null;
        try {
//            Uri uri = Uri.parse("android.resource://"+mContext.getPackageName()+"/"+ R.raw.m2psolutions_pub);
            Uri uri = Uri.parse("android.resource://"+mContext.getPackageName()+"/raw/"+"m2psolutions_pub.cer");

//            File filePublicKey = new File(String.valueOf(uri));
//            System.out.println("getAbsoluteFile:: "+filePublicKey.getAbsolutePath()+"  filePublicKey::"+uri);
//            reader = new BufferedReader(new FileReader(filePublicKey.getAbsolutePath()));
            AssetManager am = mContext.getAssets();
//        String[] files = am.list("Files");
//        System.out.println("files:: "+files);
//        File filePublicKey = new File(files[0]);

            InputStream fis = am.open("m2psolutions_pub.cer");
//            File filePublicKey = createFileFromInputStream(fis,"m2psolutions_pub.cer");
//            reader = new BufferedReader(new FileReader(filePublicKey));
            int size = fis.available();
            reader = new BufferedReader(new InputStreamReader(fis, "UTF-8"));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.startsWith("-----BEGIN PUBLIC KEY-----") && !line.startsWith("-----END PUBLIC KEY-----")) {
                    sb.append(line);
                }
            }
            System.out.println("StringBuilder:: "+sb);
            byte[] publicKeyBytes = new byte[0];
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                publicKeyBytes = Base64.getDecoder().decode(sb.toString());
                System.out.println("publicKeyBytes::"+publicKeyBytes);
            }
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
            return keyFactory.generatePublic(publicKeySpec);
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
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
        System.out.println("requestData:: "+requestData);
        String data = " {'token':" + "'"+this.generateDigitalSignedToken(requestData) +"'"+ ", 'body': "
                +"'" +this.encryptData(requestData, sessionKeyByte, messageRefNo)+"'" + ", 'entity':"
                +"'" +this.encryptKey(entity.getBytes())+"'" + ",'key': " +"'" +this.encryptKey(sessionKeyByte)+"'" + ",'refNo':"
                + "'"+messageRefNo+"'}";
        System.out.println(data);
        return data;

    }

    /*public static void main(String[] args) {

        String requestData = "{\r\n    \"entityId\": \"LQAPPL21\",\r\n    \"mobileNumber\": \"+919840315399\",\r\n    \"businessType\": \"LQFLEET101\",\r\n    \"entityType\": \"CUSOTMER\"\r\n}";
        TestEncryptionNew enc = new TestEncryptionNew();
        try
        {
            enc.encodeRequest(requestData, "1234123412341678", "LQFLEET101");
            Map<String, String> responseMap = new HashMap<>();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }*/
    private File createFileFromInputStream(InputStream inputStream,String my_file_name) {

        try{
            File f = new File(my_file_name);
            OutputStream outputStream = new FileOutputStream(f);
            byte buffer[] = new byte[1024];
            int length = 0;

            while((length=inputStream.read(buffer)) > 0) {
                outputStream.write(buffer,0,length);
            }

            outputStream.close();
            inputStream.close();

            return f;
        }catch (IOException e) {
            //Logging exception
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
    public String getEncriptedRequestData( String jsonData) throws Exception {
//        val enc = TestEncryptionNew(this)
        Random random = new Random();
        Long n =(long)(1000000000000000L + random.nextFloat() * 9000000000000000L);
        String encriptData=encodeRequest(jsonData, ""+n, "LQFLEET101");
        String replacedVal= encriptData.replace("'", "\"");
        return replacedVal;
    }

}
