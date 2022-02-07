package ru.mihailpro.lib.yandexotp.interfaces

/**
 * Interface used by classes implementing validation of Yandex 2FA secret
 */
interface IYandexOTPValidator {
    /**
     * Validates secret on the basis of its checksum (if available)
     *
     * @param secretHash text (secret) from passport.yandex or QR code
     */
    fun validateSecret(secretHash: String): Boolean

    /**
     * Validates secret on the basis of its checksum (if available)
     *
     * Kotlin's implementation of ChecksumIsValid from: github.com/norblik/KeeYaOtp
     *
     * License: GPLv3+
     *
     * @param secretHashBytes Base32 secret decoded into bytes from passport.yandex or QR code
     */
    fun validateSecret(secretHashBytes: ByteArray): Boolean
}