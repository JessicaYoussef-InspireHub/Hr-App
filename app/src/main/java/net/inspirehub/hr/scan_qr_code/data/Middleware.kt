package net.inspirehub.hr.scan_qr_code.data

class Middleware private constructor(encryptedInput: String) {

    val companyId: String
    val apiKey: String
    val baseUrl: String  // Changed from 'url' to be more specific

    init {
        val decryptedData = AESEncryptionUtils.decryptData(encryptedInput)
        val (cid, key, url) = parseDecryptedData(decryptedData)
        this.companyId = cid
        this.apiKey = key
        this.baseUrl = url
    }

    private fun parseDecryptedData(decryptedData: String): Triple<String, String, String> {
        val params = decryptedData.split(DATA_DELIMITER)
        require(params.size == 3) {
            "Invalid decrypted data format"
        }
        return Triple(params[0], params[1], params[2])
    }

    override fun toString(): String {
        return listOf(companyId, apiKey, baseUrl).joinToString(DATA_DELIMITER)
    }

    companion object {
        private const val DATA_DELIMITER = "|§|"

        private var instance: Middleware? = null
        fun initialize(encryptedInput: String): Middleware {
            return instance ?: synchronized(this) {
                instance ?: Middleware(encryptedInput).also { instance = it }
            }
        }
    }

}