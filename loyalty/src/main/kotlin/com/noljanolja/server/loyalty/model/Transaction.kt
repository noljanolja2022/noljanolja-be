package com.noljanolja.server.loyalty.model

data class Transaction(
    val id: Long,
    val reason: String,
    val amount: Long,
)