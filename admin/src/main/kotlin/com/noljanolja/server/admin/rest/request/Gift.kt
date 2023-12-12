package com.noljanolja.server.admin.rest.request

data class UpdateGiftRequest(
    val isActive: Boolean,
    val price: Long,
    val isFeatured: Boolean,
    val isTodayOffer: Boolean,
    val categoryId: Long? = null
)

data class UpdateGiftCategoryReq(
    val id: Long = 0,
    val name: String,
    val image: String? = null
)