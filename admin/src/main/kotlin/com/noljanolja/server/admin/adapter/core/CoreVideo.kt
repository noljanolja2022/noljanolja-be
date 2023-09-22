package com.noljanolja.server.admin.adapter.core

data class CoreCreateVideoRequest(
    val id: String,
    val youtubeUrl: String,
    val isHighlighted: Boolean
)