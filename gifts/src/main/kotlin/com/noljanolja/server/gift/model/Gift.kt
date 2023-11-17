package com.noljanolja.server.gift.model

import java.time.Instant

data class Gift(
    val id: String,
    val giftNo: Long,
    val name: String,
    val description: String,
    val image: String,
    val endTime: Instant,
    val brand: GiftBrand,
    val price: Long,
    val retailPrice: Long,
    val isActive: Boolean = false
)