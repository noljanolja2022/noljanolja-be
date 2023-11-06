package com.nolgobuljia.server.giftbiz.model


data class GiftBizResponse<T>(
    val code: String,
    val message: String? = null,
    val result: T?
)
enum class BooleanFlag {
    Y, N
}