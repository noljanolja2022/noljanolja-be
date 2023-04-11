package com.noljanolja.server.consumer.adapter.core.request

data class PostCommentRequest(
    val comment: String,
    val commenterId: String,
)
