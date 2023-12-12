package com.noljanolja.server.admin.model

import java.time.Instant

data class Gift(
    val id: String,
    val giftNo: Long,
    val name: String,
    val description: String,
    val image: String,
    val endTime: Instant,
    val brand: GiftBrand,
    val category: GiftCategory? = null,
    val limitDay: Int,
    val price: Long,
    val retailPrice: Long,
    val isActive: Boolean = false,
    val isFeatured: Boolean = false,
    val isTodayOffer: Boolean = false
)

data class GiftBrand(
    val id: String,
    val name: String,
    var image: String,
)

data class GiftCategory(
    val id: Long,
    val name: String = "Other",
    var image: String? = null,
)