package com.noljanolja.server.consumer.rsocket

data class UserVideoComment(
    val userId: String,
    val videoId: String,
    val comment: String,
)
