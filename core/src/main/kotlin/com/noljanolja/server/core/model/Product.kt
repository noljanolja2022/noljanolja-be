package com.noljanolja.server.core.model

import com.nolgobuljia.server.giftbiz.model.GiftBizGood

data class Product(
//    val id: String = "",
    // Use this code when purchase coupon
    val code: String,
    val name: String,
    val img: String,
    val brand: String,
    val brandImg: String,
    val brandCode: String,
    val description: String,
    val realMoneyPrice: Long,
    val price: Long = 0,
    val isActive: Boolean = false,
)

fun GiftBizGood.toProduct(): Product {
    return Product(
        code = goodsCode,
        img = goodsImgB,
        brandCode = this.brandCode,
        brandImg = this.brandIconImg,
        brand = this.brandName,
        description = this.content,
        name = this.goodsName,
        realMoneyPrice = this.salePrice,
    )
}