package com.noljanolja.server.consumer.adapter.core

import com.noljanolja.server.consumer.model.Gift
import java.time.Instant

data class CoreGift(
    val id: Long,
    val name: String,
    val description: String,
    val image: String,
    val startTime: Instant,
    val endTime: Instant,
    val price: Long,
    val brand: Brand,
    val category: Category,
    val codes: List<String>,
    val total: Int,
    val remaining: Int,
) {
    data class Brand(
        val id: Long,
        val name: String,
        val image: String,
    )

    data class Category(
        val id: Long,
        val code: String,
        val image: String,
    )
}

fun CoreGift.Category.toGiftCategory() = Gift.Category(
    id = id,
    code = code,
    image = image,
)

fun CoreGift.Brand.toGiftBrand() = Gift.Brand(
    id = id,
    name = name,
    image = image,
)

fun CoreGift.toGift(
    includeCodes: Boolean = true,
) = Gift(
    id = id,
    name = name,
    description = description,
    image = image,
    startTime = startTime,
    endTime = endTime,
    total = total,
    remaining = remaining,
    brand = brand.toGiftBrand(),
    category = category.toGiftCategory(),
    price = price,
    codes = codes.takeIf { includeCodes }.orEmpty(),
    isPurchasable = remaining > 0 && Instant.now() in startTime..endTime
)
