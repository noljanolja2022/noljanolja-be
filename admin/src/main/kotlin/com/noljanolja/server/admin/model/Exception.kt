package com.noljanolja.server.admin.model

import com.noljanolja.server.common.exception.BaseException

sealed class CoreServiceError(
    code: Int,
    message: String,
    cause: Throwable?,
) : BaseException(code, message, cause) {
    companion object {
        const val BAD_REQUEST = 400_000
        const val USER_NOT_FOUND = 404_001
        const val INTERNAL_ERROR = 500_002
    }

    class GeneralApiError(
        code: Int,
        message: String,
    ) : CoreServiceError(code, message, null)

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

sealed class OpenAIError(
    code: Int,
    message: String,
    cause: Throwable?,
) : BaseException(code, message, null) {
    companion object {
        const val BAD_REQUEST = 400_100
        const val CONNECTION_ERROR = 500_100
    }

    class BadRequest(
        message: String,
    ) : OpenAIError(
        BAD_REQUEST,
        message = "[OpenAI] $message",
        cause = null,
    )

    object ConnectionError : OpenAIError(
        CONNECTION_ERROR,
        "[OpenAI] Error connecting to OpenAI server",
        null,
    )
}