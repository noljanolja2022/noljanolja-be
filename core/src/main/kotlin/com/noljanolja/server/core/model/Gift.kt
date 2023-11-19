package com.noljanolja.server.core.model

import com.nolgobuljia.server.giftbiz.model.GiftBizGood
import com.noljanolja.server.gift.model.Gift
import com.noljanolja.server.gift.model.GiftBrand

fun GiftBizGood.toGift(): Gift {
    val res = Gift(
        id = goodsCode,
        giftNo = goodsNo,
        image = goodsImgB,
        brand = GiftBrand(
            id = brandCode,
            name = brandName,
            image = brandIconImg,
        ),
        limitDay = limitDay,
        description = this.content,
        name = this.goodsName,
        price = this.realPrice,
        retailPrice = this.realPrice,
        endTime = this.endDate,
    )
    return res
}