package com.noljanolja.server.consumer.adapter.core.request

import com.noljanolja.server.consumer.adapter.core.CoreLocalContact

data class UpsertUserContactsRequest(
    val contacts: List<CoreLocalContact>,
)
