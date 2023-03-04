package com.noljanolja.server.consumer.service

import com.noljanolja.server.consumer.adapter.core.CoreApi
import com.noljanolja.server.consumer.model.Announcement
import org.springframework.stereotype.Component

@Component
class AnnouncementService(
    private val coreApi: CoreApi,
) {
    internal suspend fun getAnnouncements(
        page: Long,
        pageSize: Long,
    ): Pair<List<Announcement>, Long> {
        return coreApi.getAnnouncements(
            page = page,
            pageSize = pageSize,
        ).let {
            Pair(it.data, it.paging.totalRecords)
        }
    }
}