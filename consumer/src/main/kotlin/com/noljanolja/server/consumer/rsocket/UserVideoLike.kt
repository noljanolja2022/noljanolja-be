package com.noljanolja.server.consumer.rsocket

import com.noljanolja.server.consumer.model.RateVideoAction

data class UserVideoLike(
    val action: RateVideoAction,
    val userId: String,
    val videoId: String,
)
