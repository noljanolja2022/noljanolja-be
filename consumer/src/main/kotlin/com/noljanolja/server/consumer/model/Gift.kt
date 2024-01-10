package com.noljanolja.server.consumer.model

import java.time.Instant

data class Gift(
    val id: String,
    val giftNo: Long,
    val name: String,
    val description: String,
    val image: String,
    val endTime: Instant,
    val price: Long,
    val limitDay: Int = 0,
    val brand: GiftBrand,
    val category: GiftCategory? = null
)

data class GiftCategory(
    val id: Long,
    val name: String?,
    val image: String? = null
)

data class GiftBrand(
    val id: String,
    val name: String,
    val image: String,
)

data class PurchasedGift(
    val id: String,
    val giftId: String,
    val qrCode: String,
    val name: String,
    val description: String,
    val image: String,
    val brand: GiftBrand,
    val category: GiftCategory? = null,
    val log: String?
)