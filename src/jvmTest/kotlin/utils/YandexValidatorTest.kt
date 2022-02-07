package utils

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import ru.mihailpro.lib.yandexotp.YandexOTPException
import ru.mihailpro.lib.yandexotp.utils.YandexOTPValidator
import utils.TestConstants.vectors
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class YandexValidatorTest {
    @Test
    fun testValidationOk() {
        for (i in 0..2) {
            assertTrue { YandexOTPValidator.validateSecret(vectors[i]) }
        }
    }

    @Test
    fun testYandexSecretValidation() {
        assertFalse { YandexOTPValidator.validateSecret(vectors[5]) }
        assertThrows<YandexOTPException> { YandexOTPValidator.validateSecret(vectors[6]) }
    }
}