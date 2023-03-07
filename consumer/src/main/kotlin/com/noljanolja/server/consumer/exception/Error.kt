package com.noljanolja.server.consumer.exception

import com.noljanolja.server.common.exception.BaseException

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