package com.noljanolja.server.core.rest.request

import com.noljanolja.server.core.model.Banner
import java.time.Instant

data class UpsertBannerRequest(
    val id: Long? = null,
    val title: String,
    val description: String,
    val content: String,
    val image: String,
    val isActive: Boolean,
    val priority: Banner.BannerPriority,
    val action: Banner.BannerAction,
    val actionUrl: String?,
    val startTime: Instant,
    val endTime: Instant,
)
