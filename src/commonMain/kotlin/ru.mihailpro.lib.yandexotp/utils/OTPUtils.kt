package ru.mihailpro.lib.yandexotp.utils

/*
    yandexotp-kotlin, a Kotlin library that generate one-time passwords for Yandex 2FA
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
    along with this plugin. If not, see <https://www.gnu.org/licenses/>.

    SPDX-License-Identifier: GPL-3.0-or-later
*/

internal object OTPUtils {
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