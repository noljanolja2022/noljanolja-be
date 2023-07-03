package com.noljanolja.server.core.repo.banner

import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface BannerRepo : CoroutineCrudRepository<BannerModel, Long> {
    @Query(
        """
            SELECT * FROM banners WHERE
            IF(:isActive IS NOT NULL, is_active = :isActive, TRUE) AND
            IF(:title IS NOT NULL AND :title <> '', title LIKE CONCAT('%',:title,'%'), TRUE)
            LIMIT :limit OFFSET :offset
        """
    )
    fun findAllBy(
        title: String? = null,
        isActive: Boolean? = null,
        limit: Int,
        offset: Int,
    ): Flow<BannerModel>

    @Query("""
        SELECT COUNT(*) FROM banners WHERE
        IF(:isActive IS NOT NULL, is_active = :isActive, TRUE)
    """)
    suspend fun countAllBy(
        isActive: Boolean? = null,
    ): Long
}