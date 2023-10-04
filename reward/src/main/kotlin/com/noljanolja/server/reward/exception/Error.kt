package com.noljanolja.server.reward.exception

import com.noljanolja.server.common.exception.BaseException

sealed class Error(
    code: Int,
    message: String,
    cause: Throwable?,
) : BaseException(code, message, cause) {
    companion object {
        const val CONFIG_NOT_FOUND = 404_009
        const val CHECKIN_CONFIG_NOT_FOUND = 404_016
        const val INVALID_VIDEO_CONFIG = 400_101

    }

    object VideoConfigNotFound : BaseException(
        CONFIG_NOT_FOUND,
        "Video config not found",
        null,
    )

    object CheckinConfigNotFound : BaseException(
        CHECKIN_CONFIG_NOT_FOUND,
        "Checkin config not found",
        null,
    )

    object InvalidVideoConfig : BaseException(
        INVALID_VIDEO_CONFIG,
        "Invalid video config",
        null,
    )
}