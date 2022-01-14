package utils

import ru.mihailpro.lib.yandexotp.data.YandexSecret
import ru.mihailpro.lib.yandexotp.utils.Base32

internal object TestConstants {
    internal val yaUris = arrayOf(
        "otpauth://yaotp/test-ya?" +
                "secret=LA2V6KMCGYMWWVEW64RNP3JA3I" +
                "&name=test" +
                "&track_id=7d60d5223c566939afbb04127e160a514f" +
                "&uid=1021603358" +
                "&pin_length=4",
        "otpauth://yafakeotp/",
        "otpauth://yaotp/test-ya?" +
                "&name=test" +
                "&uid=1021603358",
        "otpauth://yaotp/test-ya?" +
                "secret=LA2V6KMCGYMWWVEW64RNP3JA3I" +
                "&pin_length=10",
        "otpauth://yaotp/test-ya?" +
                "secret=LA2V6KMCGYMWWVEW64RNP3JA3I" +
                "&uid=1021603358"
    )

    internal val vectors = arrayOf(
        "LA2V6KMCGYMWWVEW64RNP3JA3IAAAAAAHTSG4HRZPI", // correct
        "WBVVBMPOBM4EW4RRW4JBJWIJXYAAAAAADFUTQMCVBE", // correct
        "JBGSAU4G7IEZG6OY4UAXX62JU4AAAAAAHTSG4HXU3M", // correct
        "6SB2IKNM6OBZPAVBVTOHDKS4FAAAAAAADFUTQMBTRY", // correct
        "LA2V6KMCGYMWWVEW64RNP3JA3I",                 // secret from QR - no validation
        "AA2V6KMCGYMWWVEW64RNP3JA3IAAAAAAHTSG4HRZPI", // first letter is different
        "AA2V6KMCGJA3IAAAAAAHTSG4HRZPI"               // size is wrong
    )

    internal val yaSecrets = arrayOf(
        YandexSecret(
            secret = Base32.decode("LA2V6KMCGYMWWVEW64RNP3JA3I"),
            userId = 1021603358,
            userName = "test",
            pinLength = 4
        ),
        YandexSecret(
            secret = Base32.decode("WBVVBMPOBM4EW4RRW4JBJWIJXY"),
            userId = 426326064,
            pinLength = 6
        ),
        YandexSecret(
            secret = Base32.decode("JBGSAU4G7IEZG6OY4UAXX62JU4"),
            userId = 1021603358,
            pinLength = 16
        ),
        YandexSecret(
            secret = Base32.decode("LA2V6KMCGYMWWVEW64RNP3JA3I"),
            userId = -1,
            pinLength = -1
        ),
    )
}