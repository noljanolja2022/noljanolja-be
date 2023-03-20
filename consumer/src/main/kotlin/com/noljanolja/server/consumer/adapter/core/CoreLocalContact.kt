package com.noljanolja.server.consumer.adapter.core

import com.noljanolja.server.consumer.model.LocalContact

data class CoreLocalContact(
    val name: String,
    val phones: List<String> = listOf(),
) {
    companion object {
        fun LocalContact.toCoreLocalContact() = CoreLocalContact(
            name = name,
            phones = phones,
        )
    }
}
