package com.nolgobuljia.server.giftbiz.model

import com.fasterxml.jackson.annotation.JsonFormat

data class GiftBizResponse<T>(
    val code: String,
    val message: String? = null,
    val result: T?
)
enum class BooleanFlag {
    Y, N
}

data class IndianTokenResponse(
    val access_token: String?,
    val expires_in: Long?,
    val token_type: String?,
    val scope: String?,
    val error: String?
)

data class ErrorMessage(
    val code: String,
    val level: String,
    val text: String
)

data class Meta(
    val status: String,
    val messages: List<ErrorMessage>?
)

data class IndianCoupon(
    val voucher_ref: String?,
    val voucher_settlement_ref: String?,
    val voucher_name: String?,
    val status: String?,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    val expiration_date: String?,
    val edenred_url: String?,
    val view_code: String?,
    val pin_code: String?,
    val display_codes: List<String>?
)

data class IndianCouponResponse(
    val meta: Meta,
    val data: List<IndianCoupon>?
)

data class OrderDetail(
    val order_number: String
)

data class ManualRequestData(
    val manual_quantity: Int,
    val distribution_mode: String,
    val order_details: OrderDetail
)