package com.noljanolja.server.gifts.rest.request

import java.time.Instant

data class CreateGiftRequest(
    val name: String,
    val description: String,
    val image: String,
    val codes: List<String>,
    val startTime: Instant,
    val endTime: Instant,
    val categoryId: Long,
    val brandId: Long,
    val price: Long,
)
