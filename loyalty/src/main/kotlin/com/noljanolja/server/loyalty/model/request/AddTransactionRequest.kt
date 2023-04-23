package com.noljanolja.server.loyalty.model.request

data class AddTransactionRequest(
    val point: Long,
    val reason: String,
)