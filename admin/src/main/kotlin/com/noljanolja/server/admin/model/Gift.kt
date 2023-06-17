package com.noljanolja.server.admin.model

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
        var image: String,
    )

    data class Brand(
        val id: Long,
        val name: String,
        var image: String,
    )
}

data class CreateGiftRequest(
    val name: String,
    val description: String = "",
    var image: String = "",
    val codes: List<String> = emptyList(),
    val startTime: Instant,
    val endTime: Instant,
    val categoryId: Long,
    val brandId: Long,
    val price: Long,
)

data class UpdateGiftRequest(
    val name: String? = null,
    val description: String? = null,
    var image: String? = null,
    val price: Long? = null,
    val startTime: Instant? = null,
    val endTime: Instant? = null,
)