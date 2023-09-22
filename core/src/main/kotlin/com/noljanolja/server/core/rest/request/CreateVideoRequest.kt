package com.noljanolja.server.core.rest.request


data class CreateVideoRequest(
    val id: String,
    val youtubeUrl: String,
    val isHighlighted: Boolean
)
