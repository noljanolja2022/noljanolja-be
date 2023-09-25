package com.noljanolja.server.core.rest.request

import java.time.LocalDate

data class PromoteVideoRequest (
    val autoLike: Boolean,
    val autoPlay: Boolean,
    val autoSubscribe: Boolean,
    val autoComment: Boolean,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val interactionDelay: Int
)

data class ReactToPromotedVideoReq(
    val youtubeToken: String,
    val userId: String,
)