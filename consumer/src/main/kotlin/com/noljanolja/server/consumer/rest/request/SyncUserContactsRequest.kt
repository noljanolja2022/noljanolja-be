package com.noljanolja.server.consumer.rest.request

import com.noljanolja.server.consumer.model.LocalContact

data class SyncUserContactsRequest(
    val contacts: List<LocalContact>,
)
