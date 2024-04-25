package com.agent.fasttag.encript

import android.content.Context
import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.annotation.RequiresApi
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.io.*
import java.nio.charset.StandardCharsets
import java.security.*
import java.security.spec.InvalidKeySpecException
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.*
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec


class Encryption(val context: Context) {


    @RequiresApi(Build.VERSION_CODES.O)
    @Throws(Exception::class)
    fun encodeRequest(requestData: String, messageRefNo: String, entity: String): String {
        val sessionKeyByte = generateToken()
        val data = (" token:" + generateDigitalSignedToken(requestData) + ", body: "
                + encryptData(requestData, sessionKeyByte, messageRefNo) + ", entity:"
                + encryptKey(entity.toByteArray()) + ",key: " + encryptKey(sessionKeyByte) + ", refNo:"
                + messageRefNo)
        println(data)

        return data
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @Throws(Exception::class)
    fun encryptData(requestData: String, sessionKey: ByteArray?, messageRefNo: String): String {
        val secKey: SecretKey = SecretKeySpec(sessionKey, "AES")
        val cipher = Cipher.getInstance(symmetricKeyAlgorithm)
        val ivSpec = IvParameterSpec(messageRefNo.toByteArray())
        cipher.init(Cipher.ENCRYPT_MODE, secKey, ivSpec)
        val newData = cipher.doFinal(requestData.toByteArray())
        return Base64.getEncoder().encodeToString(newData)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @Throws(Exception::class)
    fun encryptKey(sessionKey: ByteArray?): String {
        val pubKey = readPublicKeyFromFile(m2pPublicFile)
        val cipher = Cipher.getInstance(asymmetricKeyAlgorithm)
        cipher.init(Cipher.ENCRYPT_MODE, pubKey)
        val cipherData = cipher.doFinal(sessionKey)
        return Base64.getEncoder().encodeToString(cipherData)
    }

    @Throws(Exception::class)
    fun generateToken(): ByteArray {
        val generator = KeyGenerator.getInstance("AES")
        generator.init(128)
        val key = generator.generateKey()
        val testkey = "1234123412341234"
        return testkey.toByteArray()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @Throws(Exception::class)
    fun generateDigitalSignedToken(requestData: String): String {
        Security.addProvider(BouncyCastleProvider())
        val signature = Signature.getInstance(digitalSignatureAlgorithm)
        println("busPrivateFile:: $busPrivateFile")
        val privateKey = readPrivateKeyFromFile(busPrivateFile)
        signature.initSign(privateKey, SecureRandom())
        val message = requestData.toByteArray()
        signature.update(message)
        val sigBytes = signature.sign()
        return Base64.getEncoder().encodeToString(sigBytes)
    }

    @Throws(Exception::class)
    private fun readPrivateKeyFromFile(keyFileName: String): PrivateKey {
        val PRIVATE_KEY = context!!.assets.open("com.agent.fasttag.p8.pem").bufferedReader().use {
            it.readText()
        }
        val inputStream: InputStream = context!!.assets.open("com.agent.fasttag.p8.pem")
//        val newString = String(ist.readBytes(), 0, ist.readBytes().indexOf(0))
//        val input = ist.buffered()
        val buffer1 = ByteArray(6000)
        val buffer2 = ByteArray(8192)
        var bytesRead: Int
        val output = ByteArrayOutputStream()
        while (inputStream.read(buffer2).also { bytesRead = it } != -1) {
            output.write(buffer2, 0, bytesRead)
        }
        val file: ByteArray = output.toByteArray()
//        println("readPrivateKeyFromFile :: "+string)

        println("readPrivateKeyFromFile output:: "+output)
        val str = file.toString(Charsets.UTF_8)
        println("readPrivateKeyFromFile String:: "+str)

     /*   val filePrivateKey = File(string)
        val fis = FileInputStream(filePrivateKey)
        val encodedPrivateKey =
            ByteArray(filePrivateKey.length().toInt())
        ist.read(encodedPrivateKey)
        ist.close()*/

   /*     val keyFactory = KeyFactory.getInstance("RSA")
        val privateKeySpec =
            PKCS8EncodedKeySpec(str.toByteArray())
        println("privateKeySpec:: "+privateKeySpec)
       var key= keyFactory.generatePrivate(privateKeySpec)
        println("privateKeySpec  KEYY:: "+key)*/


        val pkcs8Lines = StringBuilder()
        val rdr = BufferedReader(StringReader(PRIVATE_KEY))
        var line: String?
        while (rdr.readLine().also { line = it } != null) {
            pkcs8Lines.append(line)
        }

        // Remove the "BEGIN" and "END" lines, as well as any whitespace


        // Remove the "BEGIN" and "END" lines, as well as any whitespace
        var pkcs8Pem = pkcs8Lines.toString()
        pkcs8Pem = pkcs8Pem.replace("-----BEGIN PRIVATE KEY-----", "")
        pkcs8Pem = pkcs8Pem.replace("-----END PRIVATE KEY-----", "")
        pkcs8Pem = pkcs8Pem.replace("\\s+".toRegex(), "")

        // Base64 decode the result


        // Base64 decode the result
        val pkcs8EncodedBytes: ByteArray = android.util.Base64.decode(pkcs8Pem, android.util.Base64.DEFAULT)

        println("pkcs8EncodedBytes:: "+pkcs8EncodedBytes)

        // extract the private key


        // extract the private key
        val keySpec = PKCS8EncodedKeySpec(pkcs8EncodedBytes)
        val kf = KeyFactory.getInstance("RSA")
        val privKey = kf.generatePrivate(keySpec)
        System.out.println("privKey:: "+privKey)






        return privKey
    }

/*    fun encrypt(
        textToEncrypt: String,
        publicKeyString: String
    ): String? {
        val publicKey = stringToPublicKey(publicKeyString)
        return encrypt(
            textToEncrypt = textToEncrypt,
            publicKey = publicKey
        )
    }*/
fun encrypt(
    textToEncrypt: String,
    publicKey: PublicKey
): String {
    val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
    cipher.init(Cipher.ENCRYPT_MODE, publicKey)
    val encryptedBytes = cipher.doFinal(textToEncrypt.toByteArray(StandardCharsets.UTF_8))
    return android.util.Base64.encodeToString(encryptedBytes, android.util.Base64.DEFAULT)
}

    @Throws(InvalidKeySpecException::class, NoSuchAlgorithmException::class)
    private fun stringToPublicKey(publicKeyString: String): PublicKey {
        val keyBytes: ByteArray = android.util.Base64.decode(publicKeyString, android.util.Base64.DEFAULT)
        val spec = X509EncodedKeySpec(keyBytes)
        val keyFactory = KeyFactory.getInstance("RSA")
        return keyFactory.generatePublic(spec)
    }

    @Throws(Exception::class)
    private fun readPublicKeyFromFile(keyFileName: String): PublicKey {
        val filePublicKey = File(keyFileName)
        val fis = FileInputStream(filePublicKey)
        val encodedPublicKey = ByteArray(filePublicKey.length().toInt())
        fis.read(encodedPublicKey)
        fis.close()
        val keyFactory = KeyFactory.getInstance("RSA")
        val publicKeySpec =
            X509EncodedKeySpec(encodedPublicKey)
        return keyFactory.generatePublic(publicKeySpec)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @Throws(Exception::class)
    fun decodeResponse(responseMap: Map<*, *>): String {
        val messageRefNo = responseMap["refNo"].toString()
        val sessionKey = responseMap["key"].toString()
        val token = responseMap["hash"].toString()
        val body = responseMap["body"].toString()
        return decryptMessage(
            body,
            sessionKey, token, messageRefNo
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @Throws(Exception::class)
    fun decryptMessage(
        xmlResponse: String,
        encSessionKey: String,
        token: String?,
        messageRefNo: String
    ): String {
        val sessionKey = decryptSessionKey(encSessionKey)
        return decryptWithAESKey(xmlResponse, sessionKey, messageRefNo.toByteArray())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @Throws(Exception::class)
    private fun decryptWithAESKey(inputData: String, key: ByteArray, iv: ByteArray): String {
        val cipher = Cipher.getInstance(symmetricKeyAlgorithm)
        val secKey = SecretKeySpec(key, symmetricKeyAlgorithm)
        val spec = IvParameterSpec(iv)
        cipher.init(Cipher.DECRYPT_MODE, secKey, spec)
        val newData = cipher.doFinal(Base64.getDecoder().decode(inputData))
        return String(newData)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @Throws(Exception::class)
    private fun decryptSessionKey(sessionKey: String): ByteArray {
        val privateKey = readPrivateKeyFromFile(busPrivateFile)
        val cipher = Cipher.getInstance(asymmetricKeyAlgorithm)
        cipher.init(Cipher.DECRYPT_MODE, privateKey)
        cipher.update(Base64.getDecoder().decode(sessionKey))
        return cipher.doFinal()
    }

    var userDir = System.getProperty("user.dir")
    private val m2pPublicFile =
        userDir + File.separator + "Keys" + File.separator + "m2p.yappay.in.pub"
    private val busPrivateFile =
        userDir + File.separator + "Keys" + File.separator + "prepaid.werize.com.pkcs8"
    private val symmetricKeyAlgorithm = "AES/CBC/PKCS5Padding"
    private val asymmetricKeyAlgorithm = "RSA/ECB/PKCS1Padding"
    private val digitalSignatureAlgorithm = "SHA1withRSA"
    private val pkiProvider = "BC"

   /* companion object {
        @RequiresApi(Build.VERSION_CODES.O)
        @JvmStatic
        fun main(a: Array<String>) {
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
            val requestData = "{\"entityId\": \"s3QhsIHWErcmnzkruALBtqeAjAu1\"}"
            val enc = Encryption()
            try {
                enc.encodeRequest(requestData, "1234123412341238", "FINOWERIZE")
                val responseMap: Map<String, String> = HashMap()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }*/





    fun generateKeyPair(): KeyPair {
        // Check if the RSA key pair already exists
        val kpgen = KeyPairGenerator.getInstance("RSA")
        val kpgenProv = kpgen.provider

        println("kpgenProv:: "+kpgenProv)

      /*  val keyStore = KeyStore.getInstance("RSA")
        keyStore.load(null)
        if (!keyStore.containsAlias("KEY_ALIAS")) {
            // Generate a new RSA key pair
            val keyPairGenerator = KeyPairGenerator.getInstance("RSA", "KEY_PROVIDER")
            val spec = KeyGenParameterSpec.Builder(
                "KEY_ALIAS",
                KeyProperties.PURPOSE_SIGN
                        or KeyProperties.PURPOSE_VERIFY
                        or KeyProperties.PURPOSE_ENCRYPT
                        or KeyProperties.PURPOSE_DECRYPT
            )
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1)
                .setSignaturePaddings(KeyProperties.SIGNATURE_PADDING_RSA_PKCS1)
                .setDigests(KeyProperties.DIGEST_SHA256)
                .setKeySize(2048)
                .build()
            keyPairGenerator.initialize(spec)
            return keyPairGenerator.generateKeyPair()
        } else {
            // Load the existing RSA key pair
            val privateKey = keyStore.getKey("KEY_ALIAS", null) as PrivateKey
            val publicKey = keyStore.getCertificate("KEY_ALIAS").publicKey
            return KeyPair(publicKey, privateKey)
        }*/

        return kpgen.generateKeyPair()
    }
}

