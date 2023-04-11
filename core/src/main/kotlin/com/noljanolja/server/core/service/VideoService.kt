package com.noljanolja.server.core.service

import com.noljanolja.server.common.exception.UserNotFound
import com.noljanolja.server.core.exception.Error
import com.noljanolja.server.core.model.Video
import com.noljanolja.server.core.model.VideoComment
import com.noljanolja.server.core.repo.media.*
import com.noljanolja.server.core.repo.user.UserRepo
import com.noljanolja.server.core.rest.request.CreateVideoRequest
import kotlinx.coroutines.flow.toList
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Component
@Transactional
class VideoService(
    private val videoRepo: VideoRepo,
    private val videoViewCountRepo: VideoViewCountRepo,
    private val videoUserRepo: VideoUserRepo,
    private val videoCommentRepo: VideoCommentRepo,
    private val videoChannelRepo: VideoChannelRepo,
    private val videoCategoryRepo: VideoCategoryRepo,
    private val userRepo: UserRepo,
) {
    suspend fun getVideoDetails(
        videoId: String,
    ): Video {
        val video = videoRepo.findById(videoId) ?: throw Error.VideoNotFound
        val comments = videoCommentRepo.findAllByVideoIdOrderByIdDesc(videoId, Pageable.ofSize(10)).toList()
        if (comments.isNotEmpty()) {
            val commenterIds = comments.mapTo(mutableSetOf()) { it.commenterId }
            val commenters = userRepo.findAllById(commenterIds).toList()
            comments.forEach { comment ->
                comment.commenter = commenters.first { it.id == comment.commenterId }
            }
        }
        return video.apply {
            channel = videoChannelRepo.findById(channelId)!!
            category = videoCategoryRepo.findById(categoryId)!!
            viewCount = videoViewCountRepo.getTotalViewCount(videoId)
            likeCount = videoUserRepo.countAllByIsLikedIsTrueAndVideoId(videoId)
            commentCount = videoCommentRepo.countAllByVideoId(videoId)
            this.comments = comments
        }.toVideo()
    }

    suspend fun getVideos(
        page: Int,
        pageSize: Int,
        isHighlighted: Boolean? = null,
        categoryId: String? = null,
    ): Pair<List<Video>, Long> {
        val videos = videoRepo.findAllBy(
            isHighlighted = isHighlighted,
            categoryId = categoryId,
            offset = (page - 1) * pageSize,
            limit = pageSize,
        ).toList()
        val total = videoRepo.countAllBy(
            isHighlighted = isHighlighted,
            categoryId = categoryId,
        )
        val channels = videoChannelRepo.findAllById(videos.map { it.channelId }.toSet()).toList()
        val categories = videoCategoryRepo.findAllById(videos.map { it.categoryId }.toSet()).toList()
        return Pair(videos.map { videoModel ->
            videoModel.channel = channels.first { it.id == videoModel.channelId }
            videoModel.category = categories.first { it.id == videoModel.categoryId }
            videoModel.viewCount = videoViewCountRepo.getTotalViewCount(videoModel.id)
            videoModel.likeCount = videoUserRepo.countAllByIsLikedIsTrueAndVideoId(videoModel.id)
            videoModel.commentCount = videoCommentRepo.countAllByVideoId(videoModel.id)
            videoModel.toVideo()
        }, total)
    }

    suspend fun deleteVideo(
        id: String,
    ) {
        videoRepo.findById(id) ?: throw Error.VideoNotFound
        videoRepo.deleteById(id)
    }

    suspend fun upsertVideo(
        videoInfo: CreateVideoRequest,
    ): Video {
        val channel = videoChannelRepo.save(
            (videoChannelRepo.findById(videoInfo.channel.id)
                ?: VideoChannelModel(videoInfo.channel.id).apply { this.isNewRecord = true })
                .apply {
                    title = videoInfo.channel.title
                    thumbnail = videoInfo.channel.thumbnail
                }
        )
        val category = videoCategoryRepo.save(
            (videoCategoryRepo.findById(videoInfo.category.id)
                ?: VideoCategoryModel(videoInfo.category.id).apply { this.isNewRecord = true })
                .apply {
                    title = videoInfo.category.title
                }
        )
        var isNewModel = false
        return videoRepo.save(
            ((videoRepo.findById(videoInfo.id) ?: VideoModel(
                _id = videoInfo.id,
            ).apply { isNewRecord = true; isNewModel = true }).apply {
                title = videoInfo.title
                url = videoInfo.url
                publishedAt = videoInfo.publishedAt
                thumbnail = videoInfo.thumbnail
                favoriteCount = videoInfo.favoriteCount
                channelId = videoInfo.channel.id
                categoryId = videoInfo.category.id
                duration = videoInfo.duration
                durationMs = videoInfo.durationMs
                isHighlighted = videoInfo.isHighlighted
            })
        )
            .also {
                if (isNewModel) {
                    videoViewCountRepo.save(
                        VideoViewCountModel(
                            videoId = videoInfo.id,
                            viewCount = 0,
                        )
                    )
                }
            }
            .apply {
                this.channel = channel
                this.category = category
                this.viewCount = videoViewCountRepo.getTotalViewCount(id)
                this.likeCount = videoUserRepo.countAllByIsLikedIsTrueAndVideoId(id)
                this.commentCount = videoCommentRepo.countAllByVideoId(id)
            }.toVideo()
    }

    suspend fun viewVideo(
        videoId: String,
    ) {
        if (!videoRepo.existsById(videoId)) throw Error.VideoNotFound
        val viewCountModel = videoViewCountRepo.findNewestVideoById(videoId)
            ?.takeIf { it.createdAt == LocalDate.now() }
            ?: VideoViewCountModel(
                videoId = videoId,
                viewCount = 0,
            )
        viewCountModel.viewCount++
        videoViewCountRepo.save(viewCountModel)
    }

    suspend fun getTrendingVideos(
        days: Int = 1,
        limit: Int = 10,
    ): List<Video> {
        return videoViewCountRepo.findTopTrendingVideos(
            days = days,
            limit = limit,
        ).toList().map {
            it.channel = videoChannelRepo.findById(it.channelId)!!
            it.category = videoCategoryRepo.findById(it.categoryId)!!
            it.viewCount = videoViewCountRepo.getTotalViewCount(it.id)
            it.likeCount = videoUserRepo.countAllByIsLikedIsTrueAndVideoId(it.id)
            it.commentCount = videoCommentRepo.countAllByVideoId(it.id)
            it.toVideo()
        }
    }

    suspend fun likeVideo(
        videoId: String,
        userId: String,
    ) {
        if (!videoRepo.existsById(videoId)) throw Error.VideoNotFound
        videoUserRepo.save(
            videoUserRepo.findByVideoIdAndUserId(
                videoId = videoId,
                userId = userId,
            )?.apply { isLiked = !isLiked }
                ?: VideoUserModel(
                    videoId = videoId,
                    userId = userId,
                    isLiked = true,
                )
        )
    }

    suspend fun postComment(
        comment: String,
        commenterId: String,
        videoId: String,
    ): VideoComment {
        if (!videoRepo.existsById(videoId)) throw Error.VideoNotFound
        val commenter = userRepo.findById(commenterId) ?: throw UserNotFound
        return videoCommentRepo.save(
            VideoCommentModel(
                comment = comment,
                commenterId = commenterId,
                videoId = videoId,
            )
        ).apply {
            this.commenter = commenter
        }.toVideoComment()
    }

    suspend fun getVideoComments(
        videoId: String,
        beforeCommentId: Long,
        limit: Int,
    ): List<VideoComment> {
        if (!videoRepo.existsById(videoId)) throw Error.VideoNotFound
        val comments = videoCommentRepo.findAllByVideoIdAndIdBeforeOrderByIdDesc(
            videoId = videoId,
            beforeCommentId = beforeCommentId,
            pageable = Pageable.ofSize(limit),
        ).toList()
        if (comments.isNotEmpty()) {
            val commenterIds = comments.mapTo(mutableSetOf()) { it.commenterId }
            val commenters = userRepo.findAllById(commenterIds).toList()
            comments.forEach { comment -> comment.commenter = commenters.first { it.id == comment.commenterId } }
        }
        return comments.map { it.toVideoComment() }
    }
}