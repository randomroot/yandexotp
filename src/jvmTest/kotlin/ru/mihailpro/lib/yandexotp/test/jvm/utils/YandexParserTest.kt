package ru.mihailpro.lib.yandexotp.test.jvm.utils

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import ru.mihailpro.lib.yandexotp.YandexOTPException
import ru.mihailpro.lib.yandexotp.test.jvm.utils.TestConstants.vectors
import ru.mihailpro.lib.yandexotp.test.jvm.utils.TestConstants.yaSecrets
import ru.mihailpro.lib.yandexotp.test.jvm.utils.TestConstants.yaUris
import ru.mihailpro.lib.yandexotp.utils.Base32
import ru.mihailpro.lib.yandexotp.utils.YandexOTPParser
import kotlin.test.assertEquals

class YandexParserTest {

    private val testAccountName = "test"
    private val testUsername = "Ivan Ivanov"

    @Test
    fun testYandexManualSecretParse() {
        with(yaSecrets[0]) {
            val parsedZeroVector = YandexOTPParser.parseManualSecret(
                vectors[0],
                testAccountName,
                testUsername
            )
            assertEquals(this, parsedZeroVector)
            assertEquals(this.accountName, parsedZeroVector.accountName)
            assertEquals(this.userName, parsedZeroVector.userName)
        }
        for (i in 1..2) {
            assertEquals(yaSecrets[i], YandexOTPParser.parseManualSecret(vectors[i]))
        }
    }

    @Test
    fun testYandexQRSecretParse() {
        assertEquals(yaSecrets[3], YandexOTPParser.parseQRSecret(vectors[4]))
    }

    @Test
    fun testYandexUriParse() {
        assertEquals(yaSecrets[0], YandexOTPParser.parseOtpUri(yaUris[0]))
        for (i in 1..yaUris.lastIndex) {
            assertThrows<YandexOTPException> { YandexOTPParser.parseOtpUri(yaUris[i]) }
        }
    }

    @Test
    fun testParseSecret() {
        assertEquals(
            yaSecrets[0], YandexOTPParser.parseSecret(Base32.decode(vectors[0]))
        )
        assertEquals(
            yaSecrets[0], YandexOTPParser.parseSecret(
                Base32.decode(vectors[0]),
                accountName = testAccountName
            )
        )
        assertEquals(
            yaSecrets[0], YandexOTPParser.parseSecret(
                Base32.decode(vectors[0]),
                userName = testUsername
            )
        )
    }

    @Test
    fun testParseSecretAdditional() {
        assertThrows<YandexOTPException> { YandexOTPParser.parseManualSecret("1") }
        assertThrows<YandexOTPException> { YandexOTPParser.parseManualSecret(vectors[5]) }
    }
}