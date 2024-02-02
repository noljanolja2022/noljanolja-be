package com.noljanolja.server.core.repo.media

import com.noljanolja.server.core.model.CommentStatistics
import kotlinx.coroutines.flow.Flow
import org.springframework.data.domain.Pageable
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface VideoCommentRepo : CoroutineCrudRepository<VideoCommentModel, Long> {
    fun findAllByVideoIdOrderByIdDesc(
        videoId: String,
        pageable: Pageable,
    ): Flow<VideoCommentModel>

    fun findAllByVideoIdAndIdBeforeOrderByIdDesc(
        videoId: String,
        beforeCommentId: Long,
        pageable: Pageable
    ): Flow<VideoCommentModel>

    @Query(
        """
            SELECT video_id, COUNT(id) AS comment_count
            FROM video_comments
            GROUP BY video_id;
        """
    )
    fun findCommentStatistics(): Flow<CommentStatistics>
}