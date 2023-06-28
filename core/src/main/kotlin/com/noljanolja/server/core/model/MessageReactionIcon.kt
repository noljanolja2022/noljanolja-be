package com.noljanolja.server.core.model

data class MessageReactionIcon(
    val id: Long,
    val code: String,
    val description: String,
    val isDefault: Boolean,
    val codeInactive: String,
)
