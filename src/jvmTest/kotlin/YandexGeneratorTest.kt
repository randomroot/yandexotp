import org.junit.jupiter.api.Test
import ru.mihailpro.lib.yandexotp.YandexOTPGenerator
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
        )
    )

    @Test
    fun validateYaOtp() {
        for (testCase in vectors) {
            val otp = YandexOTPGenerator.getCode(
                testCase.secret.substring(0, 26),
                testCase.pin,
                testCase.timestamp,
            )
            assertEquals(testCase.expected, otp)
        }
    }

    class Vector(val pin: String, val secret: String, val timestamp: Long, val expected: String)
}