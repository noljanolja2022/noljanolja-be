package com.noljanolja.server.consumer.adapter.core

import com.noljanolja.server.consumer.model.LocalContact

data class CoreLocalContact(
    val name: String,
    val emails: List<String> = listOf(),
    val phones: List<String> = listOf(),
) {
    companion object {
        fun LocalContact.toCoreLocalContact() = CoreLocalContact(
            name = name,
            emails = emails,
            phones = phones,
        )
    }
}
