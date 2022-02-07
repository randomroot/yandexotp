package ru.mihailpro.lib.yandexotp.interfaces

import ru.mihailpro.lib.yandexotp.data.YandexSecret

/**
 * Interface use by OTP Generators for Yandex 2FA
 */
interface IYandexOTPGenerator {
    /**
     * Generates OTP code based on current time
     * Preferred way, because it also checks PIN length
     *
     * @param yaSecret parsed Yandex secret
     * @param pin user-selected PIN code
     *
     * @return 8-character text code
     */
    fun getCode(yaSecret: YandexSecret, pin: String): String

    /**
     * Generates OTP code based on selected time
     * Preferred way, because it also checks PIN length
     *
     * @param yaSecret parsed Yandex secret
     * @param pin user-selected PIN code (4 - 16 numbers long)
     * @param timestampInSeconds selected date and time in unix seconds
     *
     * @return 8-character text code
     */
    fun getCode(yaSecret: YandexSecret, pin: String, timestampInSeconds: Long): String

    /**
     * Generates OTP code based on current time
     *
     * @param secretString text (secret) from QR code or Yandex website
     * @param pin user-selected PIN code
     * @return 8-character text code
     */
    fun getCode(secretString: String, pin: String): String

    /**
     * Generates OTP code based on selected time
     *
     * @param secretString text (secret) from QR code or Yandex website
     * @param pin user-selected PIN code
     * @param timestampInSeconds selected date and time in unix seconds
     *
     * @return 8-character text code
     */
    fun getCode(secretString: String, pin: String, timestampInSeconds: Long): String

    /**
     * Generates OTP code based on selected time
     *
     * @param secretBytes secret in Base32 bytes from QR code or Yandex website
     * @param pin user-selected PIN code
     *
     * @return 8-character text code
     */
    fun getCode(secretBytes: ByteArray, pin: String): String

    /**
     * Generates OTP code based on selected time
     *
     * @param secretBytes text (secret) from QR code or Yandex website
     * @param pin user-selected PIN code
     * @param timestampInSeconds selected date and time in unix seconds
     *
     * @return 8-character text code
     */
    fun getCode(secretBytes: ByteArray, pin: String, timestampInSeconds: Long): String
}