package com.noljanolja.server.core.service

import com.noljanolja.server.core.model.CoreAnnouncement
import com.noljanolja.server.core.model.request.CreateAnnouncementRequest
import com.noljanolja.server.core.repo.announcement.AnnouncementModel
import com.noljanolja.server.core.repo.announcement.AnnouncementRepo
import com.noljanolja.server.core.repo.announcement.toAnnouncementModel
import org.springframework.stereotype.Component
import java.util.*

@Component
class AnnouncementDS(
    private val announcementRepo: AnnouncementRepo,
) {
    suspend fun getAnnouncements(
        page: Long,
        pageSize: Long,
    ): Pair<List<CoreAnnouncement>, Long> {
        val totalRecords = announcementRepo.count()
        return Pair(
            announcementRepo.findAllAnnouncements(
                offset = (page - 1) * pageSize,
                limit = pageSize,
            ).toList().map { it.toAnnouncementModel() },
            totalRecords,
        )
    }

    suspend fun createAnnouncement(announcementReq: CreateAnnouncementRequest) {
        announcementRepo.save(
            AnnouncementModel(
                title = announcementReq.title,
                content = announcementReq.content,
                priority = announcementReq.priority,
            ).apply { isNewRecord = true }
        )
    }

    suspend fun deleteAnnouncement(announcementId: String) {
        announcementRepo.deleteById(UUID.fromString(announcementId))
    }
}