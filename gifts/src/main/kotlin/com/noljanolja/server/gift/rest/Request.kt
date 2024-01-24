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

data class IndianGiftReq(
    val voucherCode: String,
    val name: String,
    val description: String,
    val image: String,
    val brandId: String,
    val categoryId: Long,
    val price: Long,
    val isActive: Boolean
)