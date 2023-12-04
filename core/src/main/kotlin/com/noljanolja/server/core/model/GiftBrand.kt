package com.noljanolja.server.core.model

import com.nolgobuljia.server.giftbiz.model.GiftBizBrand
import com.noljanolja.server.gift.model.GiftBrand

fun GiftBizBrand.toGiftBrand(): GiftBrand {
    return GiftBrand(
        id = brandCode,
        name = brandName,
        image = brandIConImg
    )
}