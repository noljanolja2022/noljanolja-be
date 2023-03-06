package com.noljanolja.server.consumer.adapter.core.request

import com.noljanolja.server.consumer.adapter.core.CoreLocalContact

data class UpdateUserContactsRequest(
    val contacts: List<CoreLocalContact>,
)
