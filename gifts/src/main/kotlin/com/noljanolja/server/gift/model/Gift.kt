package com.noljanolja.server.gift.model

import java.time.Instant

data class Gift(
    val id: String,
    val giftNo: Long,
    val name: String,
    val description: String,
    val image: String,
    val endTime: Instant,
    val limitDay: Int,
    val brand: GiftBrand,
    val category: GiftCategory? = null,
    val price: Long,
    val retailPrice: Long,
    val isActive: Boolean = false,
    val isFeatured: Boolean = false,
    val isTodayOffer: Boolean = false
)

data class GiftLogTransaction(
    val id: String,
    val name: String,
    val image: String,
    val brand: GiftBrand
)