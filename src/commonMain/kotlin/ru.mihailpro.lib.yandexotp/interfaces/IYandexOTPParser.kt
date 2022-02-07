package ru.mihailpro.lib.yandexotp.interfaces

import ru.mihailpro.lib.yandexotp.YandexOTPException
import ru.mihailpro.lib.yandexotp.data.YandexSecret

/**
 * Interface used by Utils classes for parsing Yandex 2FA strings
 */
interface IYandexOTPParser {
    /**
     * Parses Yandex OTP URL from passport.yandex, provided for Yandex.Key App
     */
    fun parseOtpUri(yandexOtpUri: String): YandexSecret

    /**
     * Creates an object to store data obtained by parsing the secret (26 chars long)
     *
     * @param secretHash secret string from Yandex.Key QR code
     * @param accountName (optional) usually get from <accountName>@ya.ru
     * @param userName (optional) usually actual user name and surname
     *
     * @throws YandexOTPException if validation fails
     */
    fun parseQRSecret(
        secretHash: String,
        accountName: String = "",
        userName: String = ""
    ): YandexSecret

    /**
     * Creates an object to store data obtained by parsing the secret (42 chars long).
     * Pre-validates the secret on the basis of its checksum (if available)
     *
     * @param secretHash secret string copied from passport.yandex
     * @param accountName (optional) usually get from <accountName>@ya.ru
     * @param userName (optional) usually actual user name and surname
     *
     * @throws YandexOTPException if validation fails
     */
    fun parseManualSecret(
        secretHash: String,
        accountName: String = "",
        userName: String = ""
    ): YandexSecret

    /**
     * Creates an object to store data obtained by parsing the secret decoded with Base32.
     * Pre-validates the secret on the basis of its checksum (if available)
     *
     * @param secretHashBytes Base32 secret decoded into bytes from passport.yandex or QR code
     * @param accountName (optional) usually get from <accountName>@ya.ru
     * @param userName (optional) usually actual user name and surname
     *
     * @throws YandexOTPException if validation fails
     */
    fun parseSecret(
        secretHashBytes: ByteArray,
        accountName: String = "",
        userName: String = ""
    ): YandexSecret
}