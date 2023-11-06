package com.noljanolja.server.admin.model

data class Product(
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