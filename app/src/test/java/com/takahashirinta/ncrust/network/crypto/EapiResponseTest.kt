package com.takahashirinta.ncrust.network.crypto

import org.junit.Assert.assertEquals
import org.junit.Test
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

class EapiResponseTest {
    @Test
    fun encryptedWriteResponseSupportsRawBytesAndBase64() {
        val expected = "{\"code\":200,\"message\":\"成功\"}"
        val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
        cipher.init(Cipher.ENCRYPT_MODE, SecretKeySpec("e82ckenh8dichen8".toByteArray(), "AES"))
        val encrypted = cipher.doFinal((expected + "\r\n ").toByteArray(Charsets.UTF_8))

        assertEquals(expected, EapiCrypto.decryptResponse(encrypted))
        assertEquals(expected, EapiCrypto.decryptResponse(XeapiCrypto.base64Encode(encrypted)))
    }

    @Test
    fun invalidResponseLeavesPlainJsonFallbackAvailable() {
        assertEquals("", EapiCrypto.decryptResponse("{\"code\":200}".toByteArray()))
        assertEquals("", EapiCrypto.decryptResponse("!invalid-base64!"))
        assertEquals("", EapiCrypto.decryptResponse(byteArrayOf()))
        assertEquals("", EapiCrypto.decryptResponse(""))
    }
}
