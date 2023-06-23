package com.noljanolja.server.consumer.adapter.core

import com.noljanolja.server.consumer.model.Banner
import java.time.Instant

data class CoreBanner(
    val id: Long,
    val title: String,
    val description: String,
    val content: String,
    val image: String,
    val isActive: Boolean,
    val priority: BannerPriority,
    val action: BannerAction,
    val startTime: Instant,
    val endTime: Instant,
) {
    enum class BannerPriority {
        LOW,
        MEDIUM,
        HIGH,
        URGENT,
    }

    enum class BannerAction {
        NONE,
        LINK,
        SHARE,
        CHECKIN,
    }
}

fun CoreBanner.toBanner() = Banner(
    id = id,
    title = title,
    description = description,
    content = content,
    image = image,
    isActive = isActive && Instant.now() in startTime..endTime,
    priority = Banner.BannerPriority.valueOf(priority.name),
    action = Banner.BannerAction.valueOf(action.name),
    startTime = startTime,
    endTime = endTime,
)