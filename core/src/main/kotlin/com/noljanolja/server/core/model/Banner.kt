package com.noljanolja.server.core.model

import java.time.Instant

data class Banner(
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