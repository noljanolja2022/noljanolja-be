package com.noljanolja.server.admin.model

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
    val actionUrl: String?,
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

data class CoreBanner(
    val id: Long,
    val title: String,
    val description: String,
    val content: String,
    val image: String,
    val isActive: Boolean,
    val priority: BannerPriority,
    val action: BannerAction,
    val actionUrl: String?,
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
    isActive = isActive,
    priority = Banner.BannerPriority.valueOf(priority.name),
    action = Banner.BannerAction.valueOf(action.name),
    actionUrl = actionUrl,
    startTime = startTime,
    endTime = endTime,
)

data class UpsertBannerRequest(
    val id: Long? = null,
    val title: String,
    val description: String,
    val content: String,
    var image: String?,
    val isActive: Boolean,
    val priority: Banner.BannerPriority,
    val action: Banner.BannerAction,
    val actionUrl: String?,
    val startTime: Instant,
    val endTime: Instant,
)