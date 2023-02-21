package com.noljanolja.server.admin.rest

import com.noljanolja.server.common.exception.BaseException

sealed class AuthServiceError(
    code: Int,
    message: String,
    cause: Throwable?,
) : BaseException(code, message, cause) {
    companion object {
        const val BAD_REQUEST = 400_001
        const val INTERNAL_ERROR = 500_001
    }

    class AuthServiceBadRequest(
        message: String? = null,
    ) : AuthServiceError(
        BAD_REQUEST,
        "[Auth] " + (message ?: "Bad request"),
        null,
    )

    object AuthServiceInternalError : AuthServiceError(
        INTERNAL_ERROR,
        "[Auth] Internal Error",
        null,
    )
}

sealed class CoreServiceError(
    code: Int,
    message: String,
    cause: Throwable?,
) : BaseException(code, message, cause) {
    companion object {
        const val BAD_REQUEST = 400_002
        const val USER_NOT_FOUND = 404_001
        const val INTERNAL_ERROR = 500_002
    }

    class CoreServiceBadRequest(message: String? = null) : CoreServiceError(
        BAD_REQUEST,
        "[Core] " + (message ?: "Bad request"),
        null,
    )

    object UserNotFound : CoreServiceError(
        USER_NOT_FOUND,
        "[Core] User not found",
        null
    )

    object CoreServiceInternalError : CoreServiceError(
        INTERNAL_ERROR,
        "[Core] Internal Error",
        null,
    )
}

sealed class AdminError(
    code: Int,
    message: String,
    cause: Throwable?,
) : BaseException(code, message, cause) {
    companion object {
        const val UNAUTHORIZED = 401_001
        const val FORBIDDEN = 403_001
    }

    object UnauthorizedError : AdminError(
        UNAUTHORIZED,
        "Your login token is invalid or has been expired",
        null,
    )

    object ForbiddenError : AdminError(
        FORBIDDEN,
        "You don't have permission to access this route",
        null,
    )
}