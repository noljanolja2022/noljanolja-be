package com.noljanolja.server.common.model

data class Pagination(
    val page: Int,
    val pageSize: Int,
    val total: Long,
)
