package com.noljanolja.server.reward.exception

import com.noljanolja.server.common.exception.BaseException

sealed class Error(
    code: Int,
    message: String,
    cause: Throwable?,
) : BaseException(code, message, cause) {
    companion object {
        const val CONFIG_NOT_FOUND = 404_001
        const val INVALID_VIDEO_CONFIG = 400_200
    }

    object ConfigNotFound : BaseException(
        CONFIG_NOT_FOUND,
        "Config not found",
        null,
    )

    object InvalidVideoConfig : BaseException(
        INVALID_VIDEO_CONFIG,
        "Invalid video config",
        null,
    )
}