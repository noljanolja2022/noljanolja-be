package com.noljanolja.server.consumer.adapter.core.request

data class UpsertPushTokenRequest(
    val userId: String,
    val deviceToken: String,
    val deviceType: String,
)
