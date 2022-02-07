package utils

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import ru.mihailpro.lib.yandexotp.YandexOTPException
import ru.mihailpro.lib.yandexotp.utils.YandexOTPParser
import utils.TestConstants.vectors
import utils.TestConstants.yaSecrets
import utils.TestConstants.yaUris
import kotlin.test.assertEquals

class YandexParserTest {

    @Test
    fun testYandexManualSecretParse() {
        for (i in 0..2) {
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
        for (i in 1..3) {
            assertThrows<YandexOTPException> { YandexOTPParser.parseOtpUri(yaUris[i]) }
        }
    }
}