package com.noljanolja.server.common.exception

/**
 * Base exception to be extended
 */
open class BaseException(
    open val code: Int,
    override val message: String,
    override val cause: Throwable?,
) : Exception(message, cause)

// 400 exceptions

/**
 * Default Bad Request exception
 */
class DefaultBadRequestException(
    cause: Throwable?,
) : BaseException(
    code = 400_000,
    message = "Bad Request",
    cause = cause,
)

/**
 * Exception when request param is invalid
 */
class InvalidParamsException(
    paramName: String
) : BaseException(
    code = 400_001,
    message = "Invalid $paramName",
    cause = IllegalArgumentException("Invalid $paramName")
)

/**
 * Exception when request body is missing
 */
object RequestBodyRequired : BaseException(
    code = 400_002,
    message = "Request body required",
    cause = null,
)

/**
 * Exception when server could not parse the request data: params, body, ...
 */
class SerializationException(
    message: String
) : BaseException(
    code = 400_003,
    message = message,
    cause = null,
)

// 401 exceptions

/**
 * Default Unauthorized exception
 */
class DefaultUnauthorizedException(
    cause: Throwable? = null,
) : BaseException(
    code = 401_000,
    message = "Unauthorized",
    cause = cause,
)

class InvalidTokenProvidedException(
    cause: Throwable? = null,
) : BaseException(
    code = 401_001,
    message = "Invalid Token provided",
    cause = cause,
)

class NoTokenProvidedException(
    cause: Throwable? = null,
) : BaseException(
    code = 401_002,
    message = "No Token provided",
    cause = cause,
)

// 404 exceptions

/**
 * Default Not Found exception
 */
class DefaultNotFoundException(
    cause: Throwable?,
) : BaseException(
    code = 404_000,
    message = "Not Found",
    cause = cause,
)

/**
 * Exception when user is not found
 */
object UserNotFound : BaseException(
    code = 404_001,
    message = "User not found",
    cause = null
)

// 500 exceptions

/**
 * Default Internal Server Exception
 */
class DefaultInternalErrorException(
    cause: Throwable?,
) : BaseException(
    code = 500_000,
    message = "Internal Server Error",
    cause = cause,
)

/**
 * Exception when calling external server
 */
class ExternalServiceException(
    cause: Throwable?,
) : BaseException(
    code = 500_001,
    message = "Internal Server Error",
    cause = cause,
)
