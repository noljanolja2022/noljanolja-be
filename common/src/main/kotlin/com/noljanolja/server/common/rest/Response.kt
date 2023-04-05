package com.noljanolja.server.common.rest

import com.fasterxml.jackson.annotation.JsonInclude
import com.noljanolja.server.common.model.Pagination

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Response<T>(
    val code: Int = 0,
    val message: String = "Success",
    val data: T? = null,
    val pagination: Pagination? = null,
)
