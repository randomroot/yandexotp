package ru.mihailpro.lib.yandexotp.utils

object OTPUtils {
    fun intFromBigEndian(startIndex: Int, array: ByteArray): Int =
        (array[startIndex].toInt() and 0x7f shl 24
                or (array[startIndex + 1].toInt() and 0xff shl 16)
                or (array[startIndex + 2].toInt() and 0xff shl 8)
                or (array[startIndex + 3].toInt() and 0xff))

    private const val LONG_BYTE_SIZE = 8
    fun longFromBigEndian(startIndex: Int, array: ByteArray): Long {
        var value = array[startIndex].toLong() and 0x7f
        for (i in startIndex + 1 until startIndex + LONG_BYTE_SIZE) {
            value = (value shl 8) or (array[i].toLong() and 0xff)
        }
        return value
    }
}