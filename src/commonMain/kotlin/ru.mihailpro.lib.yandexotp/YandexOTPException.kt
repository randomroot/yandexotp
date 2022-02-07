package ru.mihailpro.lib.yandexotp

import ru.mihailpro.lib.yandexotp.data.YandexOTPErrors

/**
 * Thrown to indicate that YandexOTP methods has been passed an illegal or inappropriate argument
 */
class YandexOTPException(
    /**
     * Code for external exception handlers
     */
    errorCode: YandexOTPErrors,
    /**
     * The detail message
     */
    message: String?
) : IllegalArgumentException(message)