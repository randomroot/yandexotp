package ru.mihailpro.lib.yandexotp.test.jvm

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import ru.mihailpro.lib.yandexotp.YandexOTPException
import ru.mihailpro.lib.yandexotp.YandexOTPGenerator
import ru.mihailpro.lib.yandexotp.data.YandexOTPErrors
import ru.mihailpro.lib.yandexotp.data.YandexSecret
import ru.mihailpro.lib.yandexotp.utils.Base32
import kotlin.test.assertEquals

class YandexGeneratorTest {

    @Suppress("SpellCheckingInspection")
    private val vectors = arrayOf(
        Vector("5239", "6SB2IKNM6OBZPAVBVTOHDKS4FAAAAAAADFUTQMBTRY", 1641559648L, "umozdicq"),
        Vector("7586", "LA2V6KMCGYMWWVEW64RNP3JA3IAAAAAAHTSG4HRZPI", 1581064020L, "oactmacq"),
        Vector("7586", "LA2V6KMCGYMWWVEW64RNP3JA3IAAAAAAHTSG4HRZPI", 1581090810L, "wemdwrix"),
        Vector(
            "5210481216086702",
            "JBGSAU4G7IEZG6OY4UAXX62JU4AAAAAAHTSG4HXU3M",
            1581091469L,
            "dfrpywob"
        ),
        Vector(
            "5210481216086702",
            "JBGSAU4G7IEZG6OY4UAXX62JU4AAAAAAHTSG4HXU3M",
            1581093059L,
            "vunyprpd"
        ),
        Vector(
            "5210481216086702",
            "JBGSAU4G7IEZG6OY4UAXX62JU4",
            1581093059L,
            "vunyprpd"
        )
    )

    private val testYaSecret = YandexSecret(
        secret = Base32.decode(vectors[0].secret),
        userId = -1,
        pinLength = vectors[0].pin.length
    )

    @Test
    fun validateYaOtp() {
        for (testCase in vectors) {
            val otp = YandexOTPGenerator.getCode(
                testCase.secret,
                testCase.pin,
                testCase.timestamp,
            )
            assertEquals(testCase.expected, otp)
        }
    }

    @Test
    fun validateYaOtpFromSecret() {
        assertEquals(
            vectors[0].expected, YandexOTPGenerator.getCode(
                yaSecret = testYaSecret,
                pin = vectors[0].pin,
                timestampInSeconds = vectors[0].timestamp
            )
        )
    }

    @Test
    fun throwsWrongSizeSecret() {
        val exception = assertThrows<YandexOTPException> {
            YandexOTPGenerator.getCode(
                "MZXW6===",
                ""
            )
        }
        assertEquals(YandexOTPErrors.INVALID_SECRET_LENGTH, exception.errorCode)
    }

    @Test
    fun throwsOutOfRangePinLength() {
        val exceptionSmall: YandexOTPException = assertThrows {
            YandexOTPGenerator.getCode(
                testYaSecret,
                "1"
            )
        }
        val exceptionBig: YandexOTPException = assertThrows {
            YandexOTPGenerator.getCode(
                testYaSecret,
                "123456789012345678"
            )
        }
        assertEquals(YandexOTPErrors.INVALID_PIN_LENGTH, exceptionSmall.errorCode)
        assertEquals(YandexOTPErrors.INVALID_PIN_LENGTH, exceptionBig.errorCode)
    }

    @Test
    fun throwsUnexpectedPinLength() {
        val exception: YandexOTPException = assertThrows {
            YandexOTPGenerator.getCode(
                testYaSecret,
                "12345"
            )
        }
        assertEquals(YandexOTPErrors.UNEXPECTED_PIN_LENGTH, exception.errorCode)
    }

    class Vector(val pin: String, val secret: String, val timestamp: Long, val expected: String)
}