package com.noljanolja.server.consumer.rest.request

data class PostCommentRequest(
    val comment: String,
    val youtubeToken: String
)
