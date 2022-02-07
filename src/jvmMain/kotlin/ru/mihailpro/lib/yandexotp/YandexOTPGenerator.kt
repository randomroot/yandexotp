package ru.mihailpro.lib.yandexotp

import ru.mihailpro.lib.yandexotp.data.SECRET_BYTES_LENGTH
import ru.mihailpro.lib.yandexotp.data.YA_CRYPTO_HASH_ALGORITHM
import ru.mihailpro.lib.yandexotp.data.YA_HASH_ALGORITHM
import ru.mihailpro.lib.yandexotp.data.YA_OTP_LENGTH
import ru.mihailpro.lib.yandexotp.data.YA_OTP_TIME_PERIOD_SECONDS
import ru.mihailpro.lib.yandexotp.data.YandexOTPErrors
import ru.mihailpro.lib.yandexotp.data.YandexSecret
import ru.mihailpro.lib.yandexotp.interfaces.IYandexOTPGenerator
import ru.mihailpro.lib.yandexotp.utils.Base32
import ru.mihailpro.lib.yandexotp.utils.OTPUtils
import java.security.MessageDigest
import kotlin.math.floor
import kotlin.math.pow

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
 * Implements operations of generating a one-time code for Yandex authentication
 */
object YandexOTPGenerator : IYandexOTPGenerator {
    private const val EN_ALPHABET_LENGTH: Long = 26

    override fun getCode(yaSecret: YandexSecret, pin: String): String =
        getCode(yaSecret, pin, System.currentTimeMillis() / 1000)

    override fun getCode(yaSecret: YandexSecret, pin: String, timestampInSeconds: Long): String {
        validatePinLength(pin)
        if (pin.length != yaSecret.pinLength) {
            throw YandexOTPException(
                YandexOTPErrors.UNEXPECTED_PIN_LENGTH,
                "The length of the provided PIN does not match the expected length"
            )
        }

        return getCode(yaSecret.secret, pin, timestampInSeconds)
    }

    override fun getCode(secretString: String, pin: String): String =
        getCode(secretString, pin, System.currentTimeMillis() / 1000)

    override fun getCode(secretString: String, pin: String, timestampInSeconds: Long): String =
        getCode(Base32.decode(secretString), pin, timestampInSeconds)

    override fun getCode(secretBytes: ByteArray, pin: String): String =
        getCode(secretBytes, pin, System.currentTimeMillis() / 1000)

    override fun getCode(secretBytes: ByteArray, pin: String, timestampInSeconds: Long): String =
        getCode(secretBytes, pin.toByteArray(), timestampInSeconds)

    private fun getCode(secretBytes: ByteArray, pin: ByteArray, timestampInSeconds: Long): String {
        if (secretBytes.size < SECRET_BYTES_LENGTH) {
            throw YandexOTPException(YandexOTPErrors.INVALID_SECRET_LENGTH, "Wrong secret size")
        }

        val secret = if (secretBytes.size > SECRET_BYTES_LENGTH) {
            secretBytes.copyOfRange(0, SECRET_BYTES_LENGTH)
        } else {
            secretBytes
        }

        val counter = floor(timestampInSeconds.toDouble() / YA_OTP_TIME_PERIOD_SECONDS).toLong()
        var keyHash = MessageDigest.getInstance(YA_HASH_ALGORITHM).digest(pin + secret)

        if (keyHash[0] == 0.toByte()) {
            keyHash = keyHash.copyOfRange(0, keyHash.size)
        }

        val otpNumericCode = HOTPGenerator.code(
            keyHash, YA_CRYPTO_HASH_ALGORITHM, counter,
            OTPUtils::longFromBigEndian
        )
        return getStringRepresentation(otpNumericCode)
    }

    private fun getStringRepresentation(otpCode: Long): String {
        val chars = CharArray(YA_OTP_LENGTH)
        var code = otpCode % (EN_ALPHABET_LENGTH * 1.0).pow(YA_OTP_LENGTH).toLong()

        for (i in YA_OTP_LENGTH - 1 downTo 0) {
            chars[i] = 'a' + (code % EN_ALPHABET_LENGTH).toInt()
            code /= EN_ALPHABET_LENGTH
        }

        return String(chars)
    }

    private fun validatePinLength(pin: String) {
        if (pin.length !in 4..16) {
            throw YandexOTPException(
                YandexOTPErrors.INVALID_PIN_LENGTH,
                "PIN length must be between 4 and 16 symbols"
            )
        }
    }
}