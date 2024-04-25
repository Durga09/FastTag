package com.agent.fasttag.encript

import android.os.Build
import androidx.annotation.RequiresApi
import java.io.File
import java.io.FileInputStream
import java.security.KeyFactory
import java.security.PrivateKey
import java.security.spec.PKCS8EncodedKeySpec
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec


class Decryption {
    /**
     * @param args
     */
    var userDir = System.getProperty("user.dir")
    private val busPrivateFile =
        userDir + File.separator + "Keys" + File.separator + "privatekye.pkcs8"
    private val symmetricKeyAlgorithm = "AES/CBC/PKCS5Padding"
    private val asymmetricKeyAlgorithm = "RSA/ECB/PKCS1Padding"
    private val digitalSignatureAlgorithm = "SHA1withRSA"
    private val pkiProvider = "BC"
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
        val secKey = SecretKeySpec(key, "AES")
        val spec = IvParameterSpec(iv)
        cipher.init(Cipher.DECRYPT_MODE, secKey, spec)
        val newData = cipher.doFinal(Base64.getDecoder().decode(inputData))
        return String(newData)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @Throws(Exception::class)
    private fun decryptSessionKey(sessionKey: String): ByteArray {
        val privateKey = readPrivateKeyFromFile(busPrivateFile)
        //        System.out.println(privateKey);
        val cipher = Cipher.getInstance(asymmetricKeyAlgorithm)
        cipher.init(Cipher.DECRYPT_MODE, privateKey)
        cipher.update(Base64.getDecoder().decode(sessionKey))
        return cipher.doFinal()
    }

    @Throws(Exception::class)
    private fun readPrivateKeyFromFile(keyFileName: String): PrivateKey {
        val filePrivateKey = File(keyFileName)
        val fis = FileInputStream(filePrivateKey)
        val encodedPrivateKey =
            ByteArray(filePrivateKey.length().toInt())
        fis.read(encodedPrivateKey)
        fis.close()
        val keyFactory = KeyFactory.getInstance("RSA")
        val privateKeySpec =
            PKCS8EncodedKeySpec(encodedPrivateKey)
        return keyFactory.generatePrivate(privateKeySpec)
    }

    companion object {
        @Throws(Exception::class)
        @JvmStatic
        fun main(args: Array<String>) {
            val test = Decryption()
            val responseMap: MutableMap<String, String> = HashMap()
            responseMap["refNo"] = "1234123412341238"
            responseMap["key"] =
                "H89TOB7fsn0qPNb4ntXGQDJggDIZjNUcI8qDCqIbj4f1/CjVcazgULbAVIGh5VaM5ZTDlbZIaJnKQeVcqdrGOnx9PX/whSWtkXXTrH04BSb22jYTSKUyHBoRMHTDKbpOc8wbiOaMpEf3u9TMu9rq6KlN8rRj5tKGh3VkLk6QzB0="
            responseMap["entity"] =
                "dBnHQmbZzRulStit5y8gz4M/woIVTBJurM/qPf/sPt3rJzHxJGPOtFPAWYwScu9ha9u9LDCmK+KYIybDn42EZ5xpvHE+woF6O6ntW3O34O7ezVpDJPRtY4/c6C79lmWbG9h0wlE2O2xkU3z1TSQV236w2LjKWo2/3Gy2BVdcwrU="
            responseMap["hash"] =
                "WQVUZx73mXDLlR1jW9MGl3R4PYKl9vitXhj5NgQheApmO+bdWE0/SqNJiC5Wv9d8PEeEMfOI9wD0E+ENgOOEEF9hdD4GBXBD1XZI51C2fO9M1bCzPdGzMdAd61vmjlzAyi0A+RKjZvvE2vW/Oo4MdvdBRDrbYJZBWo7O41OnRnE="
            responseMap["body"] = "7KCgIDjl32KPc+jj+tjqfM89vdfPtl8idgZXtuLpNqEECILsT1n4BbgnCea2nhg3"
            println(test.decodeResponse(responseMap))
        }
    }
}
