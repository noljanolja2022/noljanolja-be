package com.noljanolja.server.core.model

data class Pagination(
    val page: Int,
    val pageSize: Int,
    val total: Int,
)
