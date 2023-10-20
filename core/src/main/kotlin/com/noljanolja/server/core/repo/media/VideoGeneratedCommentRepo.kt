package com.noljanolja.server.core.repo.media

import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface VideoGeneratedCommentRepo : CoroutineCrudRepository<VideoGeneratedComment, Long> {
    suspend fun deleteAllByVideoId(id: String)

    fun findAllByVideoId(id: String): Flow<VideoGeneratedComment>
}