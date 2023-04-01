package com.noljanolja.server.core.repo.media

import kotlinx.coroutines.flow.Flow
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface VideoRepo : CoroutineCrudRepository<VideoModel, String> {
    fun findAllBy(
        pageable: Pageable,
    ): Flow<VideoModel>
}