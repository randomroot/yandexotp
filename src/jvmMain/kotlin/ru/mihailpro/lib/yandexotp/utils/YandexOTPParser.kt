package ru.mihailpro.lib.yandexotp.utils

import ru.mihailpro.lib.yandexotp.YandexOTPException
import ru.mihailpro.lib.yandexotp.data.SECRET_BYTES_LENGTH
import ru.mihailpro.lib.yandexotp.data.SECRET_CHARS_LENGTH
import ru.mihailpro.lib.yandexotp.data.URI_LENGTH
import ru.mihailpro.lib.yandexotp.data.YA_HOST_ID
import ru.mihailpro.lib.yandexotp.data.YandexOTPErrors
import ru.mihailpro.lib.yandexotp.data.YandexSecret
import ru.mihailpro.lib.yandexotp.interfaces.IYandexOTPParser
import java.io.UnsupportedEncodingException
import java.net.URI
import java.net.URLDecoder
import java.nio.ByteBuffer
import java.nio.ByteOrder

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

private fun List<String>.getOrEmpty(index: Int): String {
    return getOrElse(index) { "" }
}

private fun String.decodeToUTF8(): String = URLDecoder.decode(this, "UTF-8")

/**
 * Provides methods for Yandex 2FA secret and uri parsing
 */
object YandexOTPParser : IYandexOTPParser {

    private const val YA_OTP_QUERY_SECRET = "secret"
    private const val YA_OTP_QUERY_UID = "uid"
    private const val YA_OTP_QUERY_PIN_LENGTH = "pin_length"
    private const val YA_OTP_QUERY_NAME = "name"

    override fun parseOtpUri(yandexOtpUri: String): YandexSecret =
        parseOtpUri(URI.create(yandexOtpUri))

    /**
     * Parses Yandex OTP URL from passport.yandex, provided for Yandex.Key App
     */
    @Suppress("MemberVisibilityCanBePrivate")
    fun parseOtpUri(yandexOtpUri: URI): YandexSecret {
        if (yandexOtpUri.host == YA_HOST_ID) {
            with(splitQuery(yandexOtpUri)) {
                if (
                    containsKey(YA_OTP_QUERY_SECRET) &&
                    containsKey(YA_OTP_QUERY_UID) &&
                    containsKey(YA_OTP_QUERY_PIN_LENGTH)
                ) {
                    return YandexSecret(
                        secret = Base32.decode(getValue(YA_OTP_QUERY_SECRET)),
                        userId = getValue(YA_OTP_QUERY_UID).toLong(),
                        userName = getOrDefault(YA_OTP_QUERY_NAME, null),
                        pinLength = getValue(YA_OTP_QUERY_PIN_LENGTH).toInt()
                    )
                }
            }
        }

        throw YandexOTPException(
            YandexOTPErrors.INVALID_SECRET_URI, "The provided OTP URI is not a valid Yandex 2FA URI"
        )
    }

    override fun parseQRSecret(
        secretHash: String,
        accountName: String,
        userName: String
    ): YandexSecret = parseSecret(SECRET_CHARS_LENGTH, secretHash, accountName, userName)

    override fun parseManualSecret(
        secretHash: String,
        accountName: String,
        userName: String
    ): YandexSecret = parseSecret(URI_LENGTH, secretHash, accountName, userName)

    private fun parseSecret(
        length: Int,
        secretHash: String,
        accountName: String,
        userName: String
    ): YandexSecret {
        if (secretHash.length == length) {
            return parseSecret(Base32.decode(secretHash), accountName, userName)
        }

        throw YandexOTPException(
            YandexOTPErrors.INVALID_SECRET_LENGTH,
            "Secret length must be $length chars"
        )
    }

    override fun parseSecret(
        secretHashBytes: ByteArray,
        accountName: String,
        userName: String
    ): YandexSecret {
        // If secret comes from QR code - we don't know any additional user info
        if (secretHashBytes.size == SECRET_BYTES_LENGTH) {
            return YandexSecret(secretHashBytes, userId = -1, pinLength = -1)
        }

        val secret = secretHashBytes.copyOfRange(0, SECRET_BYTES_LENGTH)
        if (YandexOTPValidator.validateSecret(secretHashBytes)) {
            val userId =
                ByteBuffer.wrap(
                    secretHashBytes.copyOfRange(
                        SECRET_BYTES_LENGTH,
                        SECRET_BYTES_LENGTH + 8
                    )
                )
                    .order(ByteOrder.BIG_ENDIAN)
                    .getLong(0)
            val pinLength = ((secretHashBytes[secretHashBytes.size - 2].toInt() and 0xFF) shr 4) + 1

            return YandexSecret(secret, userId, accountName, userName, pinLength)
        }

        throw YandexOTPException(
            YandexOTPErrors.INVALID_SECRET_CHECKSUM, "Secret hash is not valid"
        )
    }

    /**
     * Parse a URI String into Name-Value Collection
     * https://stackoverflow.com/a/51024552
     */
    @Throws(UnsupportedEncodingException::class)
    private fun splitQuery(url: URI): Map<String, String> {

        val queryPairs = LinkedHashMap<String, String>()

        url.query.split("&".toRegex())
            .dropLastWhile { it.isEmpty() }
            .map { it.split('=') }
            .map { it.getOrEmpty(0).decodeToUTF8() to it.getOrEmpty(1).decodeToUTF8() }
            .forEach { (key, value) ->
                queryPairs[key] = value
            }

        return queryPairs
    }
}