package com.noljanolja.server.consumer.adapter.core

import java.time.Instant

data class CoreNotification (
    val id: Long = 0,
    val userId: String,
    val title: String,
    val type: String,
    val body: String,
    val image: String,
    val data: String,
    val isRead: Boolean,
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now(),
)