package com.nolgobuljia.server.giftbiz.model


data class ShowBizCouponResponse(
    val code: String,
    val message: String? = null,
    val result: ShowBizCoupon
)

data class ShowBizCoupon(
    val orderNo: String,
    val pinNo: String? = null,
    val couponImgUrl: String? = null
)