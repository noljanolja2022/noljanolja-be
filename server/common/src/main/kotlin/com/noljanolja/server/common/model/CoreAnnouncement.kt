package com.noljanolja.server.common.model

import com.noljanolja.server.common.util.InstantSerializer
import com.noljanolja.server.common.util.PrioritySerializer
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class CoreAnnouncement(
    val id: String,
    val title: String,
    val content: String,
    @Serializable(with = PrioritySerializer::class)
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
//TODO: move this to core