package com.noljanolja.server.core.rest.response

import com.noljanolja.server.core.model.Pagination
import com.noljanolja.server.core.model.User

data class GetUsersResponseData(
    val users: List<User>,
    val pagination: Pagination,
)