package com.noljanolja.server.consumer.rsocket

data class UserVideoProgress(
    val userId: String,
    val videoId: String,
    val sessionId: String,
    val progressPercentage: Double,
)
