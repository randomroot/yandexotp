package ru.mihailpro.lib.yandexotp.interfaces

internal interface IHOTPGenerator {
    fun getHash(secret: ByteArray, algorithm: String, counter: Long): ByteArray
    fun <T> code(
        secret: ByteArray,
        algorithm: String,
        counter: Long,
        converter: (startIndex: Int, array: ByteArray) -> T
    ): T

    fun getText(otp: Long, digits: Int): String
}