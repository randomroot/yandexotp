package ru.mihailpro.lib.yandexotp.data

/**
 * Stores parsed Yandex OTP secret
 */
class YandexSecret(
    val secret: ByteArray,
    userId: Long,
    val accountName: String? = null,
    val userName: String? = null,
    pinLength: Int,
) {
    var userId: Long = userId
        private set
    var pinLength: Int = pinLength
        private set

    internal fun update(userId: Long, pinLength: Int): YandexSecret {
        this.userId = userId
        this.pinLength = pinLength
        return this
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as YandexSecret

        if (!secret.contentEquals(other.secret)) return false
        if (userId != other.userId) return false
        if (pinLength != other.pinLength) return false

        return true
    }

    override fun hashCode(): Int {
        var result = secret.contentHashCode()
        result = 31 * result + userId.hashCode()
        result = 31 * result + pinLength
        return result
    }
}