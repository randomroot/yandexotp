package ru.mihailpro.lib.yandexotp.misc

import ru.mihailpro.lib.yandexotp.interfaces.IHOTPGenerator
import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import kotlin.math.pow

class HOTPGenerator {
    companion object : IHOTPGenerator {
        @JvmStatic
        override fun <T> code(
            secret: ByteArray,
            algorithm: String,
            counter: Long,
            converter: (startIndex: Int, array: ByteArray) -> T
        ): T {
            val hash = getHash(secret, algorithm, counter)
            val offset = hash[hash.size - 1].toInt() and 0xf
            return converter(offset, hash)
        }

        @JvmStatic
        override fun getHash(secret: ByteArray, algorithm: String, counter: Long): ByteArray {
            val key = SecretKeySpec(secret, "RAW")
            val counterBytes =
                ByteBuffer.allocate(8).order(ByteOrder.BIG_ENDIAN).putLong(counter).array()
            val mac = Mac.getInstance(algorithm)
            mac.init(key)
            return mac.doFinal(counterBytes)
        }

        @JvmStatic
        override fun getText(otp: Long, digits: Int): String {
            val code = otp % 10.0.pow(digits).toInt()
            val res = StringBuilder(code.toString())
            digits.javaClass
            while (res.length < digits) {
                res.insert(0, "0")
            }

            return res.toString()
        }
    }
}