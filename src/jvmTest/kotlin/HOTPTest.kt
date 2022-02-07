import org.junit.jupiter.api.Test
import ru.mihailpro.lib.yandexotp.HOTPGenerator
import ru.mihailpro.lib.yandexotp.utils.OTPUtils
import kotlin.test.assertEquals


class HOTPTest {
    // https://tools.ietf.org/html/rfc4226#page-32
    private val vectors = arrayOf(
        "755224", "287082",
        "359152", "969429",
        "338314", "254676",
        "287922", "162583",
        "399871", "520489"
    )

    private val secret = byteArrayOf(
        0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38, 0x39, 0x30,
        0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38, 0x39, 0x30
    )

    @Test
    fun vectorsMatch() {
        for (i in vectors.indices) {
            assertEquals(
                vectors[i], HOTPGenerator.getText(
                    otp = HOTPGenerator.code(
                        secret,
                        algorithm = "HmacSHA1",
                        counter = i.toLong(),
                        converter = OTPUtils::intFromBigEndian
                    ).toLong(),
                    digits = 6
                )
            )
        }
    }
}