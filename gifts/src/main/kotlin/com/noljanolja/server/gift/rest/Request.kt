package com.noljanolja.server.gift.rest

data class UpdateGiftCategoryReq(
    val id: Long = 0,
    val name: String,
    val image: String? = null
)

data class UpdateGiftReq(
    val name: String? = null,
    val isActive: Boolean = false,
    val categoryId: Long,
    val price: Long
)