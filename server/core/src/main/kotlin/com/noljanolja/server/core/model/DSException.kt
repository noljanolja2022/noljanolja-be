package com.noljanolja.server.core.model

import com.noljanolja.server.common.exception.BaseException
import java.util.UUID

sealed class ConversationError(
    code: Int,
    message: String,
    cause: Throwable?,
): BaseException(code, message, cause) {
    class ConversationNotfound(conversationId: Long? = null): ConversationError(
        400_021,
        "Conversation not found $conversationId",
        null,
    )

    object InvalidConversationId : ConversationError(
        400_022,
        "Invalid conversation id",
        null,
    )

    class ParticipantsNotfound(ids: List<UUID>): ConversationError(
        400_031,
        "Participants not found $ids",
        null,
    )
}

sealed class MessageError(
    code: Int,
    message: String,
    cause: Throwable?,
): BaseException(code, message, cause) {
    object InvalidContent: MessageError(
        400_061,
        "Invalid message content",
        null,
    )
}

sealed class AttachmentError(
    code: Int,
    message: String,
    cause: Throwable?,
): BaseException(code, message, cause) {
    object InvalidFile : AttachmentError(
        400_051,
        "Invalid file",
        null,
    )

    object ExceedMaxSize : AttachmentError(
        400_052,
        "File exceed max size",
        null,
    )
}