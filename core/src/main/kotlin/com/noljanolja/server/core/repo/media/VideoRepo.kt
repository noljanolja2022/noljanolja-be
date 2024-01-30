package com.noljanolja.server.core.repo.media

import kotlinx.coroutines.flow.Flow
import org.springframework.data.domain.Pageable
import org.springframework.data.r2dbc.repository.Modifying
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface VideoRepo : CoroutineCrudRepository<VideoModel, String> {
    @Query(
        """
            SELECT * 
            FROM videos 
            WHERE id = :id AND 
                IF (:includeDeleted IS NULL OR :includeDeleted IS FALSE, deleted_at IS NULL, TRUE) AND
                IF (:includeDeactivated IS NULL OR :includeDeactivated IS FALSE, is_deactivated IS FALSE, TRUE) AND
                IF (:includeUnavailableVideos IS NULL OR :includeUnavailableVideos IS FALSE, (available_from IS NULL OR (available_from IS NOT NULL AND available_from <= NOW())) AND (available_to IS NULL OR (available_to IS NOT NULL AND NOW() <= available_to)), TRUE)
        """
    )
    fun findByIdAndIncludeDeletedAndIncludeUnavailableVideo(
        id: String,
        includeDeleted: Boolean? = null,
        includeDeactivated: Boolean? = null,
        includeUnavailableVideos: Boolean? = null,
    ): Flow<VideoModel>

    @Query(
        """
            SELECT * 
            FROM videos 
            WHERE 
                IF(:isHighlighted IS NOT NULL, is_highlighted = :isHighlighted, TRUE) AND
                IF(:categoryId IS NOT NULL, category_id = :categoryId, TRUE) AND
                IF(:query IS NOT NULL AND :query <> '', title LIKE CONCAT('%',:query,'%'), TRUE) AND
                IF(:userId IS NOT NULL AND :isExcludeIgnoredVideos IS TRUE, 
                    id NOT IN (SELECT video_id FROM video_users WHERE is_ignored = TRUE AND user_id = :userId), 
                    TRUE) AND
                IF(:includeDeleted IS NULL OR :includeDeleted IS FALSE, deleted_at IS NULL, TRUE) AND
                IF (:includeDeactivated IS NULL OR :includeDeactivated IS FALSE, is_deactivated IS FALSE, TRUE) AND
                IF (:includeUnavailableVideos IS NULL OR :includeUnavailableVideos IS FALSE, (available_from IS NULL OR (available_from IS NOT NULL AND available_from <= NOW())) AND (available_to IS NULL OR (available_to IS NOT NULL AND NOW() <= available_to)), TRUE)
            ORDER BY created_at DESC
            LIMIT :limit OFFSET :offset
        """
    )
    fun findAllBy(
        isHighlighted: Boolean? = null,
        categoryId: String? = null,
        query: String? = null,
        userId: String? = null,
        isExcludeIgnoredVideos: Boolean? = null,
        includeDeleted: Boolean? = null,
        includeDeactivated: Boolean? = null,
        includeUnavailableVideos: Boolean? = null,
        offset: Int,
        limit: Int,
    ): Flow<VideoModel>

    @Query(
        """
            SELECT * 
            FROM videos 
            WHERE
                (COALESCE(:ids) IS NULL OR 
                id NOT IN (:ids)) AND
                IF(:userId IS NOT NULL AND :isExcludeIgnoredVideos IS TRUE, id NOT IN (SELECT video_id FROM video_users WHERE is_ignored = TRUE AND user_id = :userId), TRUE) AND
                IF (:includeDeleted IS NULL OR :includeDeleted IS FALSE, deleted_at IS NULL, TRUE) AND
                IF (:includeDeactivated IS NULL OR :includeDeactivated IS FALSE, is_deactivated IS FALSE, TRUE) AND
                IF (:includeUnavailableVideos IS NULL OR :includeUnavailableVideos IS FALSE, (available_from IS NULL OR (available_from IS NOT NULL AND available_from <= NOW())) AND (available_to IS NULL OR (available_to IS NOT NULL AND NOW() <= available_to)), TRUE)
            LIMIT :limit
        """
    )
    fun findAllByIdNotIn (
        ids: List<String>?,
        limit: Int,
        userId: String? = null,
        isExcludeIgnoredVideos: Boolean? = null,
        includeDeleted: Boolean? = null,
        includeDeactivated: Boolean? = null,
        includeUnavailableVideos: Boolean? = null,
    ): Flow<VideoModel>

    @Query(
        """
            SELECT COUNT(*) 
            FROM videos 
            WHERE 
                IF(:isHighlighted IS NOT NULL, is_highlighted = :isHighlighted, TRUE) AND
                IF(:categoryId IS NOT NULL, category_id = :categoryId, TRUE) AND
                IF(:query IS NOT NULL AND :query <> '', title LIKE CONCAT('%',:query,'%'), TRUE) AND
                IF(:userId IS NOT NULL AND :isExcludeIgnoredVideos IS TRUE, 
                    id NOT IN (SELECT video_id FROM video_users WHERE is_ignored = TRUE AND user_id = :userId), 
                    TRUE) AND
                IF (:includeDeleted IS NULL OR :includeDeleted IS FALSE, deleted_at IS NULL, TRUE) AND
                IF (:includeDeactivated IS NULL OR :includeDeactivated IS FALSE, is_deactivated IS FALSE, TRUE) AND
                IF (:includeUnavailableVideos IS NULL OR :includeUnavailableVideos IS FALSE, (available_from IS NULL OR (available_from IS NOT NULL AND available_from <= NOW())) AND (available_to IS NULL OR (available_to IS NOT NULL AND NOW() <= available_to)), TRUE)
        """
    )
    suspend fun countAllBy(
        isHighlighted: Boolean? = null,
        categoryId: String? = null,
        query: String? = null,
        userId: String? = null,
        isExcludeIgnoredVideos: Boolean? = null,
        includeDeleted: Boolean? = null,
        includeDeactivated: Boolean? = null,
        includeUnavailableVideos: Boolean? = null,
    ): Long

    @Query(
        """
            SELECT * 
            FROM videos 
            WHERE
                id IN (:ids) AND
                IF(:userId IS NOT NULL AND :isExcludeIgnoredVideos IS TRUE, 
                    id NOT IN (SELECT video_id FROM video_users WHERE is_ignored = TRUE AND user_id = :userId), 
                    TRUE) AND
                IF (:includeDeleted IS NULL OR :includeDeleted IS FALSE, deleted_at IS NULL, TRUE) AND
                IF (:includeDeactivated IS NULL OR :includeDeactivated IS FALSE, is_deactivated IS FALSE, TRUE) AND
                IF (:includeUnavailableVideos IS NULL OR :includeUnavailableVideos IS FALSE, (available_from IS NULL OR (available_from IS NOT NULL AND available_from <= NOW())) AND (available_to IS NULL OR (available_to IS NOT NULL AND NOW() <= available_to)), TRUE)
        """
    )
    fun findByIds(
        ids: List<String>,
        userId: String? = null,
        isExcludeIgnoredVideos: Boolean? = null,
        includeDeleted: Boolean? = null,
        includeDeactivated: Boolean? = null,
        includeUnavailableVideos: Boolean? = null,
    ): Flow<VideoModel>

    @Modifying
    @Query(
        """
            UPDATE `videos`
            SET `like_count`    = :likeCount,
                `comment_count` = :commentCount,
                `view_count`    = :viewCount
            WHERE id = :videoId;
        """
    )
    suspend fun updateCommonStatistics(videoId: String, viewCount: Long, likeCount: Long, commentCount: Long)

    @Modifying
    @Query(
        """
            UPDATE `videos`
            SET `view_count`    = `view_count` + 1
            WHERE id = :videoId;
        """
    )
    suspend fun addViewCount(videoId: String)

    @Modifying
    @Query(
        """
            UPDATE `videos`
            SET `like_count`    = `like_count` + 1
            WHERE id = :videoId;
        """
    )
    suspend fun addLikeCount(videoId: String)

    @Modifying
    @Query(
        """
            UPDATE `videos`
            SET `like_count`    = `like_count` - 1
            WHERE id = :videoId;
        """
    )
    suspend fun deductLikeCount(videoId: String)

    @Modifying
    @Query(
        """
            UPDATE `videos`
            SET deleted_at = NOW()
            WHERE id = :videoId;
        """
    )
    suspend fun softDeleteById(videoId: String)
}