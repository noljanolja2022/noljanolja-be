package com.noljanolja.server.consumer.model

import java.time.Instant

data class Gift(
    val id: Long,
    val name: String,
    val description: String,
    val image: String,
    val startTime: Instant,
    val endTime: Instant,
    val price: Long,
    val brand: GiftBrand,
    val category: GiftCategory,
    val total: Int,
    val remaining: Int,
    val isPurchasable: Boolean,
)

data class GiftBrand(
    val id: Long,
    val name: String,
    val image: String,
)

data class GiftCategory(
    val id: Long,
    val code: String,
    val image: String,
)

data class MyGift(
    val id: Long,
    val name: String,
    val description: String,
    val image: String,
    val brand: GiftBrand,
    val category: GiftCategory,
    val code: String,
)