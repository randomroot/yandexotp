@file:Suppress("SpellCheckingInspection")

import ru.mihailpro.lib.yandexotp.Base32
import kotlin.test.Test
import kotlin.test.assertEquals

class Base32Test {
    data class Vector(val original: String, val base32: String)

    private val vectors = arrayOf(
        Vector(
            "The quick brown fox jumps over the lazy dog.",
            "KRUGKIDROVUWG2ZAMJZG653OEBTG66BANJ2W24DTEBXXMZLSEB2GQZJANRQXU6JAMRXWOLQ="
        ),
        Vector("foo", "MZXW6==="),
        Vector("foob", "MZXW6YQ="),
        Vector("fooba", "MZXW6YTB"),
        Vector("foobar", "MZXW6YTBOI======")
    )

    @Test
    fun testEncode() {
        vectors.forEach { checkEncodeToString(it) }
    }

    @Test
    fun testDecode() {
        vectors.forEach { checkDecodeToString(it) }
    }

    private fun checkEncodeToString(vector: Vector) {
        val result = Base32.encode(vector.original.asciiToByteArray())
        assertEquals(vector.base32, result)
    }

    private fun checkDecodeToString(vector: Vector) {
        val result = Base32.decode(vector.base32)
        assertEquals(vector.original, result.decodeToString())
    }

    private fun String.asciiToByteArray() = ByteArray(length) {
        get(it).code.toByte()
    }
}