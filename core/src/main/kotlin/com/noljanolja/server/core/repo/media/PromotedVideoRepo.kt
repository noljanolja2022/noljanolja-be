package com.noljanolja.server.core.repo.media

import kotlinx.coroutines.flow.Flow
import org.springframework.data.domain.Pageable
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface PromotedVideoRepo : CoroutineCrudRepository<PromotedVideoModel, Long> {

    fun findAllBy(pageable: Pageable): Flow<PromotedVideoModel>

    @Query(
        """
        SELECT videos.* FROM promoted_videos inner join videos ON promoted_videos.video_id = videos.id LIMIT :limit OFFSET :offset
    """
    )
    fun findAllBy(
        offset: Int,
        limit: Int,
    ): Flow<VideoModel>

    @Query(
        """
        SELECT video_id from promoted_videos WHERE CURRENT_DATE() NOT BETWEEN start_date AND end_date
    """
    )
    fun findAllOutdatedVideos(): Flow<Long>
}