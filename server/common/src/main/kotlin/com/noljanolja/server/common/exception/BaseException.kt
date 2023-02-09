package com.noljanolja.server.common.exception

open class BaseException(
    open val code: Int,
    override val message: String,
    override val cause: Throwable?,
) : Exception(message, cause)

data class DefaultBadRequestException(
    override val cause: Throwable?,
) : BaseException(
    code = 400_000,
    message = "Bad Request",
    cause = cause,
)

data class InvalidParamsException(
    val paramName: String
) : BaseException(
    code = 400_001,
    message = "Invalid $paramName",
    cause = IllegalArgumentException("Invalid $paramName")
)

data class DefaultNotFoundException(
    override val cause: Throwable?,
) : BaseException(
    code = 404_000,
    message = "Not Found",
    cause = cause,
)

data class DefaultInternalErrorException(
    override val cause: Throwable?,
) : BaseException(
    code = 500_000,
    message = "Internal Server Error",
    cause = cause,
)

data class ExternalServiceException(
    override val cause: Throwable?,
) : BaseException(
    code = 500_001,
    message = "Internal Server Error",
    cause = cause,
)

sealed class FirebaseException(
    code: Int,
    message: String,
    cause: Throwable?,
) : BaseException(code, message, cause) {
    companion object {
        const val FAILED_TO_VERIFY_TOKEN = 401_001
    }

    class FailedToVerifyToken(
        cause: Throwable? = null,
    ) : FirebaseException(
        FAILED_TO_VERIFY_TOKEN,
        "[Firebase] Failed to verify id token",
        cause,
    )
}