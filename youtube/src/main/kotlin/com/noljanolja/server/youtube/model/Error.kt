package com.noljanolja.server.youtube.model

import com.noljanolja.server.common.exception.BaseException

sealed class Error(
    code: Int,
    message: String,
    cause: Throwable?,
) : BaseException(code, message, cause) {
    companion object {
        const val CANNOT_TAKE_ACTION_ON_YOUTUBE = 400_200
    }

    object NoYoutubeAccountForAction: BaseException(
        CANNOT_TAKE_ACTION_ON_YOUTUBE,
        "User has no youtube account setup. Please setup first and try again",
        null
    )
}