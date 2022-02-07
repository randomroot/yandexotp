package ru.mihailpro.lib.yandexotp.test.jvm.utils

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import ru.mihailpro.lib.yandexotp.YandexOTPException
import ru.mihailpro.lib.yandexotp.data.YandexOTPErrors
import ru.mihailpro.lib.yandexotp.test.jvm.utils.TestConstants.vectors
import ru.mihailpro.lib.yandexotp.utils.YandexOTPValidator
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class YandexValidatorTest {
    @Test
    fun testValidationOk() {
        for (i in 0..4) {
            assertTrue { YandexOTPValidator.validateSecret(vectors[i]) }
        }
    }

    @Test
    fun testPrivateLeadingZeros() {
        val testedMethod =
            YandexOTPValidator::class.java.getDeclaredMethod(
                "getNumberOfLeadingZeros",
                Char::class.java
            )
        testedMethod.isAccessible = true
        assertEquals(16, testedMethod.invoke(YandexOTPValidator, 0.toChar()) as Int)
    }

    @Test
    fun testYandexSecretValidation() {
        assertFalse { YandexOTPValidator.validateSecret(vectors[5]) }
        val exception = assertThrows<YandexOTPException> {
            YandexOTPValidator.validateSecret(vectors[6])
        }
        assertEquals(YandexOTPErrors.INVALID_SECRET_LENGTH, exception.errorCode)
    }
}