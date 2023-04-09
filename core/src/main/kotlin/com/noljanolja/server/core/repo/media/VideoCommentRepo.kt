package com.noljanolja.server.core.repo.media

import kotlinx.coroutines.flow.Flow
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface VideoCommentRepo : CoroutineCrudRepository<VideoCommentModel, Long> {
    fun findAllByVideoIdOrderByIdDesc(
        videoId: String,
        pageable: Pageable,
    ): Flow<VideoCommentModel>

    fun findAllByVideoIdAndIdBeforeOrderById(
        videoId: String,
        beforeCommentId: Long,
        pageable: Pageable
    ): Flow<VideoCommentModel>

    suspend fun countAllByVideoId(
        videoId: String,
    ): Long
}