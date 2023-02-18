package com.noljanolja.server.common.rest

import com.fasterxml.jackson.annotation.JsonInclude
import kotlinx.serialization.Serializable

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Response<T>(
    val code: Int = 0,
    val message: String = "Success",
    val data: T,
    val paging: Paging? = null,
)

@Serializable
data class EmptyResponse(
    val code: Int = 0,
    val message: String = "Success",
)

@Serializable
data class Paging(
    val totalRecords: Long,
    val page: Long,
    val pageSize: Long,
)