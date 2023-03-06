package com.noljanolja.server.consumer.adapter.core.response

import com.noljanolja.server.common.model.Pagination
import com.noljanolja.server.consumer.adapter.core.CoreUser

data class GetUsersResponseData(
    val users: List<CoreUser>,
    val pagination: Pagination,
)
