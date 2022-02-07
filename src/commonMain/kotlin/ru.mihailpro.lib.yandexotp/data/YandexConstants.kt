package ru.mihailpro.lib.yandexotp.data

/**
 * Scheme indicating that the secret provided is used by Yandex 2FA
 */
const val YA_HOST_ID = "yaotp"

/**
 * Output OTP length
 */
const val YA_OTP_LENGTH = 8

/**
 * Algorithm used for Yandex OTP generation
 */
const val YA_HASH_ALGORITHM = "SHA-256"

/**
 * Crypto algorithm used for second step of Yandex OTP generation
 */
internal const val YA_CRYPTO_HASH_ALGORITHM = "HmacSHA256"

/**
 * OTP period for Yandex 2FA
 */
const val YA_OTP_TIME_PERIOD_SECONDS = 30

/**
 * Length of Yandex 2FA URI from QR code
 */
internal const val URI_LENGTH = 42

/**
 * Secret length in bytes
 */
internal const val SECRET_BYTES_LENGTH = 16

/**
 * Secret length in symbols
 */
internal const val SECRET_CHARS_LENGTH = 26


enum class YandexOTPErrors {
    /**
     * Indicates that provided Yandex secret URI does not meet the specification
     */
    INVALID_OTP_URI_FORMAT,

    /**
     * Indicates that pinLength param in OTP url is invalid
     */
    INVALID_OTP_URI_PIN_LENGTH,

    /**
     * Indicates that userId param in OTP url is invalid
     */
    INVALID_OTP_URI_USER_ID,

    /**
     * Indicates that provided Yandex secret length doesn't comply with specification
     */
    INVALID_SECRET_LENGTH,

    /**
     * Indicates that provided Yandex secret is corrupted
     */
    INVALID_SECRET_CHECKSUM,

    /**
     * Indicates that PIN passed to method has
     */
    INVALID_PIN_LENGTH,


    /**
     * Indicates that PIN length passed to a method differs from expected
     */
    UNEXPECTED_PIN_LENGTH
}