package com.noljanolja.server.consumer.model

import java.time.Instant

data class Gift(
    val id: Long,
    val name: String,
    val description: String,
    val image: String,
    val startTime: Instant,
    val endTime: Instant,
    val price: Long,
    val brand: Brand,
    val category: Category,
    val codes: List<String>,
    val total: Int,
    val remaining: Int,
    val isPurchasable: Boolean,
) {
    data class Brand(
        val id: Long,
        val name: String,
        val image: String,
    )

    data class Category(
        val id: Long,
        val code: String,
        val image: String,
    )
}

