package com.noljanolja.server.consumer.adapter.core

import com.noljanolja.server.consumer.model.Gift
import com.noljanolja.server.consumer.model.GiftBrand
import com.noljanolja.server.consumer.model.GiftCategory
import com.noljanolja.server.consumer.model.PurchasedGift
import java.time.Instant

data class CoreGift(
    val id: String,
    val giftNo: Long,
    val name: String,
    val description: String,
    val image: String,
    val endTime: Instant = Instant.now(),
    val price: Long = 0,
    val limitDay: Int = 0,
    val brand: Brand,
    val category: Category? = null,
    val qrCode: String? = null
) {
    data class Brand(
        val id: String,
        val name: String,
        val image: String,
    )

    data class Category(
        val id: Long,
        val name: String?,
        val image: String? = null
    )
}

data class CorePurchasedGift(
    val id: String,
    val giftId: String,
    val qrCode: String,
    val name: String,
    val description: String,
    val image: String,
    val brand: GiftBrand,
) {
    fun toPurchasedGift() =
        PurchasedGift(
            id = id,
            giftId = giftId,
            name = name,
            description = description,
            image = image,
            brand = brand,
            qrCode = qrCode,
        )
}

fun CoreGift.Brand.toGiftBrand() = GiftBrand(
    id = id,
    name = name,
    image = image,
)

fun CoreGift.Category.toCategory() = GiftCategory(
    id = id,
    name = name,
    image = image,
)

fun CoreGift.toGift() = Gift(
    id = id,
    giftNo = giftNo,
    name = name,
    description = description,
    image = image,
    endTime = endTime,
    brand = brand.toGiftBrand(),
    category = category?.toCategory(),
    price = price,
    limitDay = limitDay
)
