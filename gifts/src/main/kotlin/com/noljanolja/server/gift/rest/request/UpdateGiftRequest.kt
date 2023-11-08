package com.noljanolja.server.gift.rest.request

import java.time.Instant

data class UpdateGiftRequest(
    val name: String? = null,
    val description: String? = null,
    val image: String? = null,
    val price: Long? = null,
    val startTime: Instant? = null,
    val endTime: Instant? = null,
)
