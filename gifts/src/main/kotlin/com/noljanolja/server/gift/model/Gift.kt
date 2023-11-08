package com.noljanolja.server.gift.model

import com.nolgobuljia.server.giftbiz.model.GiftBizGood
import java.time.Instant

data class Gift(
    val id: Long,
    val codes: List<String>,
    val name: String,
    val description: String,
    val image: String,
    val startTime: Instant,
    val endTime: Instant,
    val category: Category,
    val brand: Brand,
    val total: Int,
    val remaining: Int,
    val price: Long,
) {
    data class Category(
        val id: Long,
        val code: String,
        val image: String,
    )

    data class Brand(
        val id: Long,
        val name: String,
        val image: String,
//        val code: String = ""
    )
}

fun GiftBizGood.toGift(): Gift {
    return Gift(
        id = goodsNo,
        codes = listOf(goodsCode),
        image = goodsImgB,
        brand = Gift.Brand(
            id = 1,
            name = "굽네치킨",
            image = "https://biz.giftishow.com/Resource/brand/20210730_175204320.jpg",
        ),
        description = this.content,
        name = this.goodsName,
        price = this.salePrice,
        remaining = rmRecvNumAmount,
        category = Gift.Category(
            id = 1,
            code = "Category",
            image = "https://biz.giftishow.com/Resource/brand/20210730_175204320.jpg"
        ),
        endTime = Instant.now(),
        startTime = Instant.now(),
        total = 10
    )
}
