package com.noljanolja.server.core.rest.request

import java.time.Instant

data class CreateVideoRequest(
    val id: String,
    val youtubeUrl: String,
    val isHighlighted: Boolean,
    val availableFrom: Instant? = null,
)
