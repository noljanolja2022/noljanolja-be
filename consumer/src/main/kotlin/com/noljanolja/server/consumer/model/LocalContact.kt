package com.noljanolja.server.consumer.model

data class LocalContact(
    val name: String,
    val phones: List<String> = listOf(),
)
