package com.noljanolja.server.admin.adapter.core

import java.time.Instant

data class CoreCreateVideoRequest(
    val id: String,
    val youtubeUrl: String,
    val isHighlighted: Boolean,
    val availableFrom: Instant? = null,
)