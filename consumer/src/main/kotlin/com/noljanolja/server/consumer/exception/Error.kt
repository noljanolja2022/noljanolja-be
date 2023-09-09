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

sealed class Error(
    code: Int,
    message: String,
    cause: Throwable?,
) : BaseException(code, message, cause) {
    companion object {
        const val FILE_NOT_FOUND = 404_002
        const val CANNOT_UPDATE_CONVERSATION_PARTICIPANT = 403_001
        const val FILE_EXCEED_MAX_SIZE = 400_010
        const val EXCEED_MAX_ATTACHMENTS_SIZE = 400_011
        const val INVALID_CONTENT_TYPE = 400_012
        const val CANNOT_UPDATE_CONVERSATION = 400_013
        const val CANNOT_TAKE_ACTION_ON_YOUTUBE = 400_014
    }

    object FileExceedMaxSize : BaseException(
        FILE_EXCEED_MAX_SIZE,
        "File upload size exceeded threshold",
        null
    )

    object ExceedMaxAttachmentsSize : BaseException(
        EXCEED_MAX_ATTACHMENTS_SIZE,
        "Exceed max attachments size",
        null
    )

    object InvalidContentType : BaseException(
        INVALID_CONTENT_TYPE,
        "Invalid content type",
        null
    )

    object FileNotFound : BaseException(
        FILE_NOT_FOUND,
        "File not found",
        null
    )

    object CannotUpdateConversation: BaseException(
        CANNOT_UPDATE_CONVERSATION,
        "Cannot update conversation",
        null,
    )

    object NoPermissionToUpdateConversationParticipant: BaseException(
        CANNOT_UPDATE_CONVERSATION_PARTICIPANT,
        "Cannot update conversation participant",
        null,
    )

    object NoYoutubeAccountForAction: BaseException(
        CANNOT_TAKE_ACTION_ON_YOUTUBE,
        "User has no youtube account setup. Please setup first and try again",
        null
    )
}