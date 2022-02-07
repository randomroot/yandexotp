@file:Suppress("SpellCheckingInspection")

package ru.mihailpro.lib.yandexotp.test.common

import ru.mihailpro.lib.yandexotp.utils.Base32
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

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

    private val exoticVectors = arrayOf("猫МЯУ6===", "//!!!===")

    @Test
    fun testEncode() {
        vectors.forEach { checkEncodeToString(it) }
    }

    @Test
    fun testDecode() {
        vectors.forEach { checkDecodeToString(it) }
    }

    @Test
    fun testEncodeWithoutPadding() {
        assertEquals(
            "MZXW6", Base32.encode(
                vectors[1].original.asciiToByteArray(), withPadding =
                false
            )
        )
    }

    @Test
    fun testDecodeEmpty() {
        assertFailsWith(IllegalArgumentException::class) { Base32.decode("=========") }
    }

    @Test
    fun testExoticStrings() {
        exoticVectors.forEach(Base32::decode)
    }

    @Test
    fun testInvalidPadding() {
        assertFailsWith(IllegalArgumentException::class) { Base32.decode("MZXW6==") }
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