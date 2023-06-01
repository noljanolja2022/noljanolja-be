package com.noljanolja.server.core.rsocket

data class UserVideoProgress(
    val userId :String,
    val videoId: String,
    val progressPercentage: Double,
    val isNewView: Boolean,
)

