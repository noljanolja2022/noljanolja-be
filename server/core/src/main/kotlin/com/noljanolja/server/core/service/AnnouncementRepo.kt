package com.noljanolja.server.core.service

import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import java.util.UUID

interface AnnouncementRepo : CoroutineCrudRepository<Announcement, UUID> {
    @Query("SELECT * FROM announcements order by created_at desc LIMIT :limit OFFSET :offset")
    suspend fun findAllAnnouncements(
        offset: Long,
        limit: Long,
    ): List<Announcement>
}