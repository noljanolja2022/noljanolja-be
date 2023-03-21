package com.noljanolja.server.core.exception

import com.noljanolja.server.common.exception.BaseException

sealed class Error(
    code: Int,
    message: String,
    cause: Throwable?,
) : BaseException(code, message, cause) {
    companion object {
        const val CONVERSATION_NOT_FOUND = 404_002
        const val MESSAGE_NOT_FOUND = 404_003
        const val ATTACHMENT_NOT_FOUND = 404_004
        const val USER_NOT_PARTICIPATE_IN_CONVERSATION = 400_001
        const val INVALID_PARTICIPANTS_SIZE = 400_002
        const val PARTICIPANTS_NOT_FOUND = 400_003
        const val UNSUPPORTED_MESSAGE_TYPE = 400_004
        const val INVALID_PHONE_NUMBER = 400_005
        const val MESSAGE_NOT_BELONG_TO_CONVERSATION = 400_006
        const val STICKER_PACK_NOT_FOUND = 400_010
        const val STICKERS_NOT_FOUND = 400_011
    }

    object ConversationNotFound : Error(
        CONVERSATION_NOT_FOUND,
        "Conversation not found",
        null,
    )

    object MessageNotFound : Error(
        MESSAGE_NOT_FOUND,
        "Message not found",
        null
    )

    object AttachmentNotFound : Error(
        ATTACHMENT_NOT_FOUND,
        "Attachment not found",
        null,
    )

    object UserNotParticipateInConversation : Error(
        USER_NOT_PARTICIPATE_IN_CONVERSATION,
        "User not participate in conversation",
        null,
    )

    object InvalidParticipantsSize : Error(
        INVALID_PARTICIPANTS_SIZE,
        "Invalid participants size for room single",
        null
    )

    object ParticipantsNotFound : Error(
        PARTICIPANTS_NOT_FOUND,
        "Some participants not found",
        null,
    )

    object UnsupportedMessageType : Error(
        UNSUPPORTED_MESSAGE_TYPE,
        "Unsupported message type",
        null,
    )

    object InvalidPhoneNumber : Error(
        INVALID_PHONE_NUMBER,
        "Invalid phone number",
        null,
    )

    object MessageNotBelongToConversation : Error(
        MESSAGE_NOT_BELONG_TO_CONVERSATION,
        "Message not belong to conversation",
        null,
    )

    object StickerPackNotFound : Error(
        STICKER_PACK_NOT_FOUND,
        "Sticker pack not found",
        null
    )

    object StickersNotFound: Error(
        STICKERS_NOT_FOUND,
        "Stickers requested not found",
        null
    )
}