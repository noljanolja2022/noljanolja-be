package com.noljanolja.server.core.rest.request

data class UpsertPushTokenRequest(
    val userId: String,
    val deviceToken: String,
    val deviceType: String,
)
