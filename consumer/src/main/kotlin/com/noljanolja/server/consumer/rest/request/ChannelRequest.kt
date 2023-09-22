package com.noljanolja.server.consumer.rest.request

data class ChannelSubscriptionRequest(
    val youtubeToken: String,
    val isSubscribing: Boolean
)