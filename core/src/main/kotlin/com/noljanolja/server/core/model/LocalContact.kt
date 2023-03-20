package com.noljanolja.server.core.model

data class LocalContact(
    val name: String,
    val phones: List<String> = listOf(),
)
