package ru.mihailpro.lib.yandexotp

import ru.mihailpro.lib.yandexotp.interfaces.IHOTPGenerator
import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
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
 * Implements HMAC-Based One-Time Password Algorithms based on RFC 4226
 * Inspired by https://github.com/beemdevelopment/Aegis
 */
internal class HOTPGenerator {
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