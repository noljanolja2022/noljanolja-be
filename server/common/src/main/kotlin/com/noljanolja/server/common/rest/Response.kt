package com.noljanolja.server.common.rest

import kotlinx.serialization.Serializable

abstract class Response<T> {
    abstract val code: Int
    abstract val message: String
    abstract val data: T
    open val paging: Paging? = null
}

@Serializable
data class Paging(
    val totalRecords: Long,
    val page: Long,
    val pageSize: Long,
)

@Serializable
data class EmptyResponse(
    val code: Int = 0,
    val message: String = "Success",
)