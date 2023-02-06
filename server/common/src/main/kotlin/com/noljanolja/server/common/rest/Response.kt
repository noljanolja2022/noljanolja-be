package com.noljanolja.server.common.rest

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Response<T>(
    val code: Int = 0,
    val message: String = "Success",
    val data: T? = null,
)
