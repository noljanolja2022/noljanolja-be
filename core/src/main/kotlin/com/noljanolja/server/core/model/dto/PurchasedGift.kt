package com.noljanolja.server.core.model.dto

import com.noljanolja.server.gift.model.GiftBrand
import com.noljanolja.server.gift.model.GiftCategory
import com.noljanolja.server.gift.repo.GiftBrandModel
import com.noljanolja.server.gift.repo.GiftModel
import com.noljanolja.server.gift.repo.GiftTransactionModel

data class PurchasedGift(
    val id: String,
    val giftId: String,
    val qrCode: String,
    val name: String,
    val description: String,
    val image: String,
    val brand: GiftBrand,
    val category: GiftCategory? = null
) {
    companion object {
        fun fromGiftModel(e: GiftModel, b: GiftBrandModel, t: GiftTransactionModel) = PurchasedGift(
            id = t.id,
            giftId = t.giftCode,
            qrCode = t.barCode ?: "",
            name = e.name,
            description = e.description,
            image = e.image,
            brand = b.toGiftBrand()
        )
    }
}

