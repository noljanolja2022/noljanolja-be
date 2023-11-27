package com.noljanolja.server.gift.rest

data class UpdateGiftCategoryReq(
    val id: Long = 0,
    val name: String,
    val image: String? = null
)

data class UpdateGiftReq(
    val isActive: Boolean = false,
    val categoryId: Long,
    val price: Long,
    val isFeatured: Boolean = false,
    val isTodayOffer: Boolean = false
)