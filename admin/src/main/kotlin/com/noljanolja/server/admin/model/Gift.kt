package com.noljanolja.server.admin.model

import java.time.Instant

data class Gift(
    val id: String,
    val giftNo: Long,
    val name: String,
    val description: String,
    val image: String,
    val endTime: Instant,
    val brand: Brand,
    val price: Long,
    val retailPrice: Long,
    val isActive: Boolean = false
) {
    data class Brand(
        val id: String,
        val name: String,
        var image: String,
    )
}

data class UpdateGiftRequest(
    val isActive: Boolean,
    val price: Long
)