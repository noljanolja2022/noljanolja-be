package com.noljanolja.server.core.rest.request

import com.noljanolja.server.core.model.Sticker

data class CreateNotificationRequest(
    val userId: String,
    val title: String,
    val body: String,
    val type: String,
    val image: String,
    val data: String,
    val isRead: Boolean
)