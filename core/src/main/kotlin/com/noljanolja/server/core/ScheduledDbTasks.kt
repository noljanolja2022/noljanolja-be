package com.noljanolja.server.core

import com.noljanolja.server.core.repo.media.VideoRepo
import com.noljanolja.server.core.service.YoutubeApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@Component
@EnableScheduling
class ScheduledDbTasks(
    private val videoRepo: VideoRepo,
    private val youtubeApi: YoutubeApi
) {
    private val logger = LoggerFactory.getLogger(ScheduledDbTasks::class.java)

    //TODO: only run jobs on video with last watch less than 30 days
    @Scheduled(cron = "0 0 10 ? * 1")
    fun syncLikeAndCommentCounts() {
        val start = Instant.now().toEpochMilli()
        logger.info("Begin updating video comments and likes count")
        val processVideos = videoRepo.findAll()
        runBlocking {
            val ids = mutableListOf<String>()
            val pv = processVideos.toList()
            for (i in pv.indices) {
                ids.add(pv[i].id)
                if (ids.size >= 10 || i == pv.size - 1) {
                    updateLikeCommentCounts(ids)
                    ids.clear()
                }
            }
        }
        logger.info("Finished Synchronizing Youtube like and comments in: ${Instant.now().toEpochMilli() - start} milliseconds")
    }

    @Transactional
    suspend fun updateLikeCommentCounts(videoIds: List<String>) {
        val res = youtubeApi.fetchVideoDetail(videoIds)
        res.items.forEach {
            videoRepo.updateLikeAndComment(it.id, it.statistics.likeCount.toLong(), it.statistics.commentCount.toLong())
        }
    }
}