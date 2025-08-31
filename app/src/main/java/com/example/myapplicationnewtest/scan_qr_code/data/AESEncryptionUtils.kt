package com.example.myapplicationnewtest.scan_qr_code.data

import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec


class AESEncryptionUtils {

    companion object {

        private val ENCRYPTION_KEY = Base64.getDecoder().decode("/uHLGNxBtGI9WutDnPfiNoGNiKjdaNivKAoVRu1t/ks=")
        private val INITIALIZATION_VECTOR = Base64.getDecoder().decode("IH+8WIrwsLOZNhUfRk6GKg==")

        fun encryptData(obj: Any): String {
            return try {
                val decryptedData = obj.toString()
                val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")  // Init Encryption Algorithm -> AES
                val keySpec = SecretKeySpec(ENCRYPTION_KEY, "AES")
                val ivSpec = IvParameterSpec(INITIALIZATION_VECTOR)
                cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec)   // init cipher obj with ENCRYPT_MODE
                val encrypted = cipher.doFinal(decryptedData.toByteArray(Charsets.UTF_8))   // doFinal -> encrypt and decrypt depends on Cipher Mode (DECRYPT_MODE, ENCRYPT_MODE)
                Base64.getEncoder().encodeToString(encrypted)  // convert encrypted from byteArray to string with Base64
            } catch (ex: Exception) {
                throw IllegalArgumentException("Encryption failed", ex)
            }
        }

        fun decryptData(encryptedInput: String): String {
            return try {
                val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding") // Init Encryption Algorithm -> AES
                val keySpec = SecretKeySpec(ENCRYPTION_KEY, "AES")
                val ivSpec = IvParameterSpec(INITIALIZATION_VECTOR)
                cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec)  // init cipher obj with DECRYPT_MODE
                val decodedInput = Base64.getDecoder().decode(encryptedInput)   // convert encryptedInput from string to byteArray
                val decrypted = cipher.doFinal(decodedInput) // doFinal -> encrypt and decrypt depends on Cipher Mode (DECRYPT_MODE, ENCRYPT_MODE)
                String(decrypted, Charsets.UTF_8)   // create string from byteArray with Encoding Code UTF_8
            } catch (ex: Exception) {
                throw IllegalArgumentException("Decryption failed", ex)
            }
        }
    }
}