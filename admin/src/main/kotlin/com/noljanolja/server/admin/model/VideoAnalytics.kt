package com.noljanolja.server.admin.model

data class TrackInfo(
    val id: String,
    val title: String,
    val thumbnail: String,
    val url: String,
    val viewCount: Long,
    val likeCount: Long,
    val favoriteCount: Long,
    val commentCount: Long,
    val rewardedPoints: Long
)

data class VideoAnalytics(
    val trackInfos: List<TrackInfo>,
    val numOfVideos: Long
)