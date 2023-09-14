package com.noljanolja.server.core.rest.request

data class LikeVideoRequest(
    val userId: String,
    val action: RateVideoAction,
)

enum class RateVideoAction {
    like, dislike, none
}