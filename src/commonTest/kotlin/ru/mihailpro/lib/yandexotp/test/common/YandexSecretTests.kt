@file:Suppress("UnusedEquals", "KotlinConstantConditions", "SENSELESS_COMPARISON")

package ru.mihailpro.lib.yandexotp.test.common

import ru.mihailpro.lib.yandexotp.data.YandexSecret
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class YandexSecretTests {
    private val testVectors = arrayOf(
        YandexSecret(
            secret = byteArrayOf(0, 1, 2, 3, 4), userId = 100, accountName
            = "testuser", userName = "test-user", pinLength = 16
        ),
        // equal to first
        YandexSecret(
            secret = byteArrayOf(0, 1, 2, 3, 4), userId = 100, accountName
            = "testuser", userName = "test-user", pinLength = 16
        ),
        // ======= PART TWO =======
        YandexSecret(
            secret = byteArrayOf(0, 1, 2), userId = 101, pinLength = 4
        ),
        // different secret
        YandexSecret(
            secret = byteArrayOf(0, 1, 2, 3), userId = 101, pinLength = 4
        ),
        // different userId
        YandexSecret(
            secret = byteArrayOf(0, 1, 2), userId = 100, pinLength = 4
        ),
        // different pinLength
        YandexSecret(
            secret = byteArrayOf(0, 1, 2), userId = 101, pinLength = 16
        )
    )

    @Test
    fun testEquals() {
        assertTrue { testVectors[0] == testVectors[0] }
        assertTrue { testVectors[0] == testVectors[1] }
        assertFalse { testVectors[0].equals(null) }
        assertFalse { testVectors[0].equals("TEST") }
        for (i in 3..5) {
            assertFalse { testVectors[2] == testVectors[i] }
        }
    }

    @Test
    fun testHashCode() {
        assertTrue { testVectors[0].hashCode() == testVectors[1].hashCode() }
        assertFalse { testVectors[1].hashCode() == testVectors[2].hashCode() }
    }
}