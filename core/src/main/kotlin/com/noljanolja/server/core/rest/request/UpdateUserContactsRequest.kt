package com.noljanolja.server.core.rest.request

import com.noljanolja.server.core.model.LocalContact

data class UpdateUserContactsRequest(
    val contacts: List<LocalContact>,
)
