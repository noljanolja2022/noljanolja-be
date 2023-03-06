package com.noljanolja.server.core.model

data class LocalContact(
    val name: String,
    val emails: List<String> = listOf(),
    val phones: List<String> = listOf(),
)
