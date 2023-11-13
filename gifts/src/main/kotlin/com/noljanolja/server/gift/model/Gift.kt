package com.noljanolja.server.gift.model

import com.nolgobuljia.server.giftbiz.model.GiftBizGood
import java.time.Instant

data class Gift(
    val id: String,
    val giftNo: Long,
    val name: String,
    val description: String,
    val image: String,
    val endTime: Instant,
    val brand: GiftBrand,
    val price: Long,
    val retailPrice: Long,
    val isActive: Boolean = false
)

fun GiftBizGood.toGift(): Gift {
    return Gift(
        id = goodsCode,
        giftNo = goodsNo,
        image = goodsImgB,
        brand = GiftBrand(
            id = brandCode,
            name = brandName,
            image = brandIconImg,
        ),
        description = this.content,
        name = this.goodsName,
        price = this.salePrice,
        retailPrice = this.realPrice,
        endTime = Instant.now(),
    )
}
