package com.noljanolja.server.core.service

import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import java.util.UUID

interface AnnouncementRepo : CoroutineCrudRepository<Announcement, UUID> {
    @Query("SELECT * FROM announcements LIMIT :limit OFFSET :offset")
    suspend fun findAllAnnouncements(
        offset: Long,
        limit: Long,
    ): List<Announcement>
}