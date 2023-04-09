package com.noljanolja.server.core.rest.request

data class CreateCommentRequest(
    val comment: String,
    val commenterId: String,
)