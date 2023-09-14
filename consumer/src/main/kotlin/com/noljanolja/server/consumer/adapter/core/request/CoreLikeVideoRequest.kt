package com.noljanolja.server.consumer.adapter.core.request

import com.noljanolja.server.consumer.model.RateVideoAction

data class CoreLikeVideoRequest(
    val action: RateVideoAction,
    val userId: String,
)
