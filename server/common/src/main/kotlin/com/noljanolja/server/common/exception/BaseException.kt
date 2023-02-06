package com.noljanolja.server.common.exception

open class BaseException(
    override val cause: Throwable?,
    override val message: String,
    open val code: Int
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
