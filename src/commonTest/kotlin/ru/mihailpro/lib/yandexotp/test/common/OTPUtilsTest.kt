package ru.mihailpro.lib.yandexotp.test.common

import ru.mihailpro.lib.yandexotp.utils.OTPUtils
import kotlin.test.Test
import kotlin.test.assertEquals

class OTPUtilsTest {
    @Suppress("ArrayInDataClass")

    @Test
    fun testIntFromBigEndian() {
        val testArray = byteArrayOf(
            -52, -109, -49, 24, 80, -115, -108, -109, 76, 100,
            -74, 93, -117, -89, 102, 127, -73, -51, -28, -80
        )
        assertEquals(1284755224, OTPUtils.intFromBigEndian(0, testArray))
    }

    @Test
    fun testLongFromBigEndian() {
        val testArray = byteArrayOf(
            117, 73, 106, -67, 106, -24, 19, 29, 87, -37, -15, 50, -38,
            -19, -53, -17, 3, -93, -71, 57, -84, 12, -71, -91, -106,
            127, 119, 54, 108, 94, 3, -83
        )
        assertEquals(7911679969317173676, OTPUtils.longFromBigEndian(13, testArray))
    }
}