package com.noljanolja.server.core.rsocket

data class UserVideoComment (
    val userId: String,
    val videoId: String,
    val comment: String,
)