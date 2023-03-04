package com.noljanolja.server.consumer.model


import com.noljanolja.server.common.util.InstantSerializer

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class Announcement(
    val id: String,
    val title: String,
    val content: String,
    val priority: Priority,
    @Serializable(with = InstantSerializer::class)
    val date: Instant,
) {
    enum class Priority {
        URGENT,
        HIGH,
        MEDIUM,
        LOW
    }
}