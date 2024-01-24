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

data class IndianGiftRequest(
    val voucherCode: String,
    val name: String,
    val description: String,
    val image: String,
    val brandId: String,
    val categoryId: Long,
    val price: Long,
    val isActive: Boolean
)