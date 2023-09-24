package com.noljanolja.server.core.repo.media

import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface PromotedVideoUserLogRepo : CoroutineCrudRepository<PromotedVideoUserLogModel, Long> {
    suspend fun findByVideoIdAndUserId(
        videoId: String,
        userId: String,
    ): PromotedVideoUserLogModel?

}