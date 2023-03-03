package com.noljanolja.server.consumer.model

import com.noljanolja.server.core.model.CoreAnnouncement
import com.noljanolja.server.common.util.InstantSerializer
import com.noljanolja.server.core.util.PrioritySerializer
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
internal data class Announcement(
    val id: String,
    val title: String,
    val content: String,
    @Serializable(with = PrioritySerializer::class)
    val priority: CoreAnnouncement.Priority,
    @Serializable(with = InstantSerializer::class)
    val date: Instant,
)

internal fun CoreAnnouncement.toAnnouncementModel() = Announcement(
    id = id,
    title = title,
    content = content,
    priority = priority,
    date = date,
)