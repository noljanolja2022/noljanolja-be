package com.noljanolja.server.consumer.rest.request

data class UpdatePushTokenRequest(
    val deviceToken: String,
)

data class UpdatePushTokenLegacyRequest(
    val deviceToken: String,
    val type: String
)
