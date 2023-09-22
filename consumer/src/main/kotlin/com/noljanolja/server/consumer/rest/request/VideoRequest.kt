package com.noljanolja.server.consumer.rest.request

import com.noljanolja.server.consumer.model.RateVideoAction

data class RateVideoRequest(
    val youtubeToken: String?,
    val action: RateVideoAction
)