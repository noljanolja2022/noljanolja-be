package com.noljanolja.server.core.rest.request

data class SubscribeChannelRequest(
    val isSubscribing: Boolean,
    val userId: String,
)