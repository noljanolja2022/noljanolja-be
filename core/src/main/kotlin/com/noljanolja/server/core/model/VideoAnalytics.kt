package com.noljanolja.server.core.model

import com.fasterxml.jackson.annotation.JsonProperty

data class TrackInfo(
    val id: String,
    val title: String,
    val thumbnail: String,
    val url: String,
    val viewCount: Long,
    val likeCount: Long,
    val commentCount: Long,
    val rewardedPoints: Long
)

data class VideoAnalytics(
    val trackInfos: List<TrackInfo>,
    val numOfVideos: Long
)

data class LikeStatistics(
    @JsonProperty("video_id")
    val videoId: String,
    @JsonProperty("like_count")
    val likeCount: Long
)

data class CommentStatistics(
    @JsonProperty("video_id")
    val videoId: String,
    @JsonProperty("comment_count")
    val commentCount: Long
)