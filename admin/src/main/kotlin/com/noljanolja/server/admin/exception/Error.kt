package com.noljanolja.server.admin.exception

import com.noljanolja.server.common.exception.BaseException

sealed class Error(
    code: Int,
    message: String,
    cause: Throwable?,
) : BaseException(code, message, cause) {
    companion object {
        const val INVALID_PROGRESS = 400_001
        const val FILE_NOT_FOUND = 404_002
    }

    object InvalidProgress : Error(
        code = INVALID_PROGRESS,
        message = "Invalid progress",
        cause = null,
    )

    object FileNotFound : BaseException(
        FILE_NOT_FOUND,
        "File not found",
        null
    )
}