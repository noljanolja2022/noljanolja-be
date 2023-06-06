package com.noljanolja.server.gifts.model

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
    )
}
