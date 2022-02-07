package ru.mihailpro.lib.yandexotp.utils

import ru.mihailpro.lib.yandexotp.YandexOTPException
import ru.mihailpro.lib.yandexotp.data.SECRET_BYTES_LENGTH
import ru.mihailpro.lib.yandexotp.data.SECRET_CHARS_LENGTH
import ru.mihailpro.lib.yandexotp.data.YandexOTPErrors
import ru.mihailpro.lib.yandexotp.interfaces.IYandexOTPValidator
import kotlin.math.min

/*
    yandexotp, a Kotlin library that generate one-time passwords for Yandex 2FA
    Copyright (C) 2022 RandomRoot

    This library is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    any later version.

    This library is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this library. If not, see <https://www.gnu.org/licenses/>.

    SPDX-License-Identifier: GPL-3.0-or-later
*/

/**
 * Provides methods for Yandex 2FA secret validation
 */
object YandexOTPValidator : IYandexOTPValidator {
    override fun validateSecret(secretHash: String): Boolean =
        validateSecret(Base32.decode(secretHash))

    override fun validateSecret(secretHashBytes: ByteArray): Boolean {
        if (secretHashBytes.size != SECRET_BYTES_LENGTH && secretHashBytes.size != SECRET_CHARS_LENGTH) {
            throw YandexOTPException(
                YandexOTPErrors.INVALID_SECRET_LENGTH,
                String.format(
                    "Invalid Yandex secret length: %d bytes",
                    secretHashBytes.size
                )
            )
        }

        // Secrets originating from a QR code do not have a checksum, so we assume those are valid
        if (secretHashBytes.size == SECRET_BYTES_LENGTH) {
            return true
        }

        val originalChecksum: Char = (
                ((secretHashBytes[secretHashBytes.size - 2].toInt() and 0x0F) shl 8)
                        or
                        (secretHashBytes[secretHashBytes.size - 1].toInt() and 0xFF)
                ).toChar()

        var accum = 0.toChar()
        var accumBits = 0
        var inputTotalBitsAvailable = secretHashBytes.size * 8 - 12
        var inputIndex = 0
        var inputBitsAvailable = 8

        while (inputTotalBitsAvailable > 0) {
            var requiredBits = 13 - accumBits
            if (inputTotalBitsAvailable < requiredBits) {
                requiredBits = inputTotalBitsAvailable
            }
            while (requiredBits > 0) {
                var curInput: Int =
                    secretHashBytes[inputIndex].toInt() and (1 shl inputBitsAvailable) - 1 and
                            0xff
                val bitsToRead = min(requiredBits, inputBitsAvailable)
                curInput = curInput shr inputBitsAvailable - bitsToRead
                accum = (accum.code shl bitsToRead or curInput).toChar()
                inputTotalBitsAvailable -= bitsToRead
                requiredBits -= bitsToRead
                inputBitsAvailable -= bitsToRead
                accumBits += bitsToRead
                if (inputBitsAvailable == 0) {
                    inputIndex += 1
                    inputBitsAvailable = 8
                }
            }
            if (accumBits == 13) {
                accum = (accum.code xor 6387).toChar()
            }
            accumBits = 16 - getNumberOfLeadingZeros(accum)
        }

        return accum == originalChecksum
    }

    private fun getNumberOfLeadingZeros(resultValue: Char): Int {
        var value = resultValue
        if (value.code == 0) {
            return 16
        }
        var n = 0
        if (value.code and 0xFF00 == 0) {
            n += 8
            value = (value.code shl 8).toChar()
        }
        if (value.code and 0xF000 == 0) {
            n += 4
            value = (value.code shl 4).toChar()
        }
        if (value.code and 0xC000 == 0) {
            n += 2
            value = (value.code shl 2).toChar()
        }
        if (value.code and 0x8000 == 0) {
            n++
        }
        return n
    }
}