package com.noljanolja.server.core.rest.request

data class PostCommentRequest(
    val comment: String,
    val commenterId: String,
)