package net.inspirehub.hr.scan_qr_code.data

import android.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec


class AESEncryptionUtils {

    companion object {

        private val ENCRYPTION_KEY =
            Base64.decode("/uHLGNxBtGI9WutDnPfiNoGNiKjdaNivKAoVRu1t/ks=", Base64.DEFAULT)

        private val INITIALIZATION_VECTOR =
            Base64.decode("IH+8WIrwsLOZNhUfRk6GKg==", Base64.DEFAULT)

        fun decryptData(encryptedInput: String): String {
            return try {
                val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding") // Init Encryption Algorithm -> AES
                val keySpec = SecretKeySpec(ENCRYPTION_KEY, "AES")
                val ivSpec = IvParameterSpec(INITIALIZATION_VECTOR)
                cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec)  // init cipher obj with DECRYPT_MODE
                val decodedInput = Base64.decode(encryptedInput, Base64.DEFAULT)  // convert encryptedInput from string to byteArray
                val decrypted = cipher.doFinal(decodedInput) // doFinal -> encrypt and decrypt depends on Cipher Mode (DECRYPT_MODE, ENCRYPT_MODE)
                String(decrypted, Charsets.UTF_8)   // create string from byteArray with Encoding Code UTF_8
            } catch (ex: Exception) {
                throw IllegalArgumentException("Decryption failed", ex)
            }
        }
    }
}