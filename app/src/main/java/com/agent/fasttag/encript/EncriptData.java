package com.agent.fasttag.encript;

import android.content.Context;
import android.os.Build;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
public class EncriptData {

  /*  public  void main(String[] a) {
        *//*
         * Security.addProvider(new BouncyCastleProvider()); Encryption test = new
         * Encryption(); try { Map<String, String> responseMap = new HashMap<>();
         * responseMap.put("refNo", "1234123412341278"); responseMap.put("key",
         * "SmYjLAA2bzIhgt4ZgOSv4jgKGltanyiYqRHQIx1lJB5dfYvPDvN/94c5Uvp8n4KfA/m790yKe1gsLNxRFRu0gaCUHBcMI05cbJ9BYf+u0q/nu25dOv9ZbQR8f9kB6mkiFJdHzKYnLDi7c/qe7fuViW9fHhma2rkDJ4bP7ihxFAyg7uBYDowUqDCN7ec1YaNakuWYZmxqoQ6hlwdd6Smnfswg43e1sdRXKsrRD5V4O0fzegF8MLbcA/d6SVEq/E+bjPcest018/mlEF84SFcYpiP3Tly46xjywfY91yB7MApvxcDRvNxqJIzS0MXe0Zk94FxZiIqLWwfvNrU0e2CStw=="
         * ); responseMap.put("entity",
         * "bUghSA8tpyWQxeCkvTPxysY9qLuV4thI6paOgrKtxclhTUQz904U4NrbNs5cE+/HgpvE8JmgSxNDTXjMX68IAbuIOHFw1TBDuisDN1QmWeHL9CY85cshgLHhbz5qKk17S/AJZXXgUKlT0+ihEeVOd47cEdOETHRTwKWxzUxXqbamo9eZBnv3qxUcbQymytMBbNIlpYP6dEl7bPJf/H/9EMPCQTgnvqmUbZd5oLuaMobxaDMd93j58hKLCsApnWVUyQvqPEvO88uDPZtnEa6ZxvMRuznE1wt1iSRiW1EGxiA8cJ5gtrNAOyfGqslHe7WQLsaP+siPiX6Pf01PbyWx2A=="
         * ); responseMap.put("hash",
         * "E7S5OVYFSsa7vabSnyEnJGvswZK13Ya06qoh3NR5jETohIN+fgKDRKkxdJM5FtfaKtnGGeMloKLzDLKN4hkV44yS8YoFdXo+6M/YFfYtZBLbchAaSN9veaffxYGImKVHKhZm9Vqlrv13uTOjZn/8rykCUhixpyZvoCaPiImb0ZMsA1jGbh1U46XOSCVK+xCRPRPUBm/8Yi3l+hIb+SRS8P/BP+D4XajSpTFvM++0bkGa3nXyETSocGlsv0bt7f4EZ1Vso9q6GWEVFjMjLGLWMsVLGA+acL8qDux1RB3XDJPyf+U1rw2DUn03hv5HGGCebX5a0d4xXIkZ+LUAek3v4w=="
         * ); responseMap.put("body",
         * "V8ngAfoxLM8s8qyklFzm9iCMl/tEfdkN0HF34qpHRtVNGMAeuGZpY4fmO7xnNw5K6tFYmWiXnsDuYukiPCU+RmaVsQ8kzZjX9+TCPpujx8+FUh+fPicf+kwE7YeASp2HjFhAHuUsnKZm1QUPfJoXy9OOVtXu8AsjOk/CCvW3bJNhtzPlXXEPkqZbSV3H+FVLxYrDJJNaQvyECWhwYG6wcXhMyomR106PHAZKcu2sn4Ux83iuH0XgQ438CTNAu3fJsZ9f4F3f7/kHierkXOMetHPF4iircgOa2s6Z+DWcqeY="
         * ); System.out.println(test.decodeResponse(responseMap)); } catch (Exception
         * e) { e.printStackTrace(); }
         *//*

        String requestData = "{\"entityId\": \"s3QhsIHWErcmnzkruALBtqeAjAu1\"}";
        Encryption enc = new Encryption(context);
        try
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                enc.encodeRequest(requestData, "1234123412341238", "FINOWERIZE");
            }
            Map<String, String> responseMap = new HashMap<>();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }*/

    public String encodeRequest(String requestData, String messageRefNo, String entity) throws Exception {
        byte[] sessionKeyByte = this.generateToken();
        String data = " token:" + this.generateDigitalSignedToken(requestData) + ", body: "
                + this.encryptData(requestData, sessionKeyByte, messageRefNo) + ", entity:"
                + this.encryptKey(entity.getBytes()) + ",key: " + this.encryptKey(sessionKeyByte) + ", refNo:"
                + messageRefNo;
        System.out.println(data);
        return data;

    }

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
        String testkey = "1234123412341234";
        byte[] symmetricKey = testkey.getBytes();
        return symmetricKey;

    }

    public String generateDigitalSignedToken(String requestData) throws Exception {

        Security.addProvider(new BouncyCastleProvider());

        Signature signature = Signature.getInstance(digitalSignatureAlgorithm);
        PrivateKey privateKey = this.readPrivateKeyFromFile(busPrivateFile);
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
        File filePrivateKey = new File(keyFileName);
        FileInputStream fis = new FileInputStream(filePrivateKey);
        byte[] encodedPrivateKey = new byte[(int) filePrivateKey.length()];
        fis.read(encodedPrivateKey);
        fis.close();
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(encodedPrivateKey);
        PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);
        return privateKey;
    }

    private PublicKey readPublicKeyFromFile(String keyFileName) throws Exception {

        File filePublicKey = new File(keyFileName);
        FileInputStream fis = new FileInputStream(filePublicKey);
        byte[] encodedPublicKey = new byte[(int) filePublicKey.length()];
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

    String userDir =System.getProperty("user.dir");

    private String m2pPublicFile = userDir+File.separator+"Keys"+File.separator+"m2p.yappay.in.pub";
    private String busPrivateFile = userDir+File.separator+"Keys"+File.separator+"prepaid.werize.com.pkcs8";

    private String symmetricKeyAlgorithm = "AES/CBC/PKCS5Padding";

    private String asymmetricKeyAlgorithm = "RSA/ECB/PKCS1Padding";

    private String digitalSignatureAlgorithm = "SHA1withRSA";

    private String pkiProvider = "BC";

}