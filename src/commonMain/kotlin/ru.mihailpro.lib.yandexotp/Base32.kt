package ru.mihailpro.lib.yandexotp

import kotlin.experimental.xor

internal object Base32 {
    private const val base32Chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567"
    private const val paddingChar = '='
    private val paddings = arrayOf(0, 1, 3, 4, 6)
    private const val charsPerGroup = 8;
    private const val bytesPerGroup = 5;

    private const val bitsPerByte = 8;
    private const val bitsPerChar = 5;

    private val base32Lookup = intArrayOf(
        0xFF, 0xFF, 0x1A, 0x1B, 0x1C, 0x1D, 0x1E, 0x1F,  // '0', '1', '2', '3', '4', '5', '6', '7'
        0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF,  // '8', '9', ':', ';', '<', '=', '>', '?'
        0xFF, 0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06,  // '@', 'A', 'B', 'C', 'D', 'E', 'F', 'G'
        0x07, 0x08, 0x09, 0x0A, 0x0B, 0x0C, 0x0D, 0x0E,  // 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O'
        0x0F, 0x10, 0x11, 0x12, 0x13, 0x14, 0x15, 0x16,  // 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W'
        0x17, 0x18, 0x19, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF,  // 'X', 'Y', 'Z', '[', '\', ']', '^', '_'
        0xFF, 0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06,  // '`', 'a', 'b', 'c', 'd', 'e', 'f', 'g'
        0x07, 0x08, 0x09, 0x0A, 0x0B, 0x0C, 0x0D, 0x0E,  // 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o'
        0x0F, 0x10, 0x11, 0x12, 0x13, 0x14, 0x15, 0x16,  // 'p', 'q', 'r', 's', 't', 'u', 'v', 'w'
        0x17, 0x18, 0x19, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF // 'x', 'y', 'z', '{', '|', '}', '~', 'DEL'
    )

    /**
     * Encodes byte array to Base32 String.
     *
     * @param bytes Bytes to encode.
     * @return Encoded byte array <code>bytes</code> as a String.
     *
     */
    internal fun encode(bytes: ByteArray, withPadding: Boolean = true): String {
        var (length, padding) = getLengthWithPadding(bytes.size, withPadding);
        val base32 = StringBuilder(length)

        var i = 0;
        var index = 0;
        var digit: Int;
        var currentByte: Int;
        var nextByte: Int

        while (i < bytes.size) {
            currentByte = if (bytes[i] >= 0) bytes[i].toInt() else bytes[i] + 256;

            /* Is the current digit going to span a byte boundary? */
            if (index > 3) {
                nextByte = if ((i + 1) < bytes.size) {
                    if (bytes[i + 1] >= 0) bytes[i + 1].toInt() else bytes[i + 1] + 256
                } else {
                    0
                }
                digit = currentByte and (0xFF shr index)
                index = (index + 5) % 8
                digit = (digit shl index).xor(nextByte shr (8 - index));
                i += 1
            } else {
                digit = (currentByte shr (8 - (index + 5))) and 0x1F
                index = (index + 5) % 8
                if (index == 0) i += 1
            }
            base32.append(base32Chars[digit])
        }

        if (withPadding) {
            while (padding > 0) {
                base32.append(paddingChar)
                padding -= 1
            }
        }

        return base32.toString()
    }

//    internal fun decode(data: String): ByteArray = decoder(
//        data.replace('8', 'L').replace('9', 'O')
//    )

    /**
     * Decodes the given Base32 String to a raw byte array.
     *
     * @param base32
     * @return Decoded <code>base32</code> String as a raw byte array.
     */
    internal fun decode(base32: String): ByteArray {
        var index = 0
        var lookup: Int
        var offset = 0
        var digit: Int

        var paddingIndex = base32.length
        while (base32[paddingIndex - 1] == paddingChar) paddingIndex -= 1
        val outputLength = paddingIndex * bitsPerChar / bitsPerByte
        if (outputLength == 0) throw IllegalArgumentException("Base32 string is too short")

        if (paddingIndex != base32.length) {
            validatePaddedString(
                inputLength = base32.length, outputLength, paddingIndex
            )
        }

        val bytes = ByteArray(outputLength)

        for (i in 0..paddingIndex) {
            lookup = base32[i] - '0';
            /* Skip chars outside the lookup table */
            if (lookup < 0 || lookup >= base32Lookup.size) {
                continue;
            }
            digit = base32Lookup[lookup];
            /* If this digit is not in the table, ignore it */
            if (digit == 0xFF) {
                continue;
            }

            if (index <= 3) {
                index = (index + 5) % 8
                if (index == 0) {
                    bytes[offset] = bytes[offset].toInt().xor(digit).toByte()
                    offset += 1
                    if (offset >= bytes.size) break;
                } else {
                    bytes[offset] = bytes[offset].xor((digit shl (8 - index)).toByte())
                }
            } else {
                index = (index + 5) % 8
                bytes[offset] = bytes[offset].xor((digit ushr index).toByte())
                offset += 1

                if (offset >= bytes.size) break;
                bytes[offset] = bytes[offset].xor((digit shl (8 - index)).toByte())
            }
        }

        return bytes
    }

    private fun getLengthWithPadding(size: Int, withPadding: Boolean): Pair<Int, Int> {
        val groupsCount = (size + bytesPerGroup - 1) / bytesPerGroup;
        val lastGroupFakeBytes = groupsCount * bytesPerGroup - size;
        val padding = paddings[lastGroupFakeBytes]

        return if (withPadding)
            Pair(groupsCount * charsPerGroup, padding)
        else
            Pair(groupsCount * charsPerGroup - padding, 0)
    }

    private fun validatePaddedString(inputLength: Int, outputSize: Int, paddingIndex: Int) {
        val (_, padding) = getLengthWithPadding(outputSize, true);
        if (inputLength - padding != paddingIndex)
            throw IllegalArgumentException("Invalid Base32 string padding length")
    }
}