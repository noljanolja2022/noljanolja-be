package com.noljanolja.server.core.repo.media

import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface VideoUserRepo : CoroutineCrudRepository<VideoUserModel, Long> {
    suspend fun findByVideoIdAndUserId(
        videoId: String,
        userId: String,
    ): VideoUserModel?

    suspend fun countAllByIsLikedIsTrueAndVideoId(
        videoId: String
    ): Long
}