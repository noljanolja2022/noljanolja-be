package com.noljanolja.server.core.model

import com.nolgobuljia.server.giftbiz.model.GiftBizGood
import com.noljanolja.server.gift.model.Gift
import com.noljanolja.server.gift.model.GiftBrand
import java.time.Instant

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