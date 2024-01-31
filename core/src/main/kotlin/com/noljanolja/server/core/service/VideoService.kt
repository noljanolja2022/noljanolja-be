package com.noljanolja.server.core.service

import com.noljanolja.server.common.exception.CustomBadRequestException
import com.noljanolja.server.common.exception.UserNotFound
import com.noljanolja.server.core.exception.Error
import com.noljanolja.server.core.model.PromotedVideoConfig
import com.noljanolja.server.core.model.Video
import com.noljanolja.server.core.model.VideoAnalytics
import com.noljanolja.server.core.model.VideoComment
import com.noljanolja.server.core.repo.media.*
import com.noljanolja.server.core.repo.user.UserRepo
import com.noljanolja.server.core.rest.request.PromoteVideoRequest
import com.noljanolja.server.core.rest.request.RateVideoAction
import com.noljanolja.server.reward.repo.VideoRewardConfigRepo
import com.noljanolja.server.youtube.model.YoutubeChannel
import com.noljanolja.server.youtube.model.YoutubeVideo
import com.noljanolja.server.youtube.model.YoutubeVideoCategory
import com.noljanolja.server.youtube.service.YoutubeApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onEmpty
import kotlinx.coroutines.flow.toList
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.Duration
import java.time.Instant
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
    private val promotedVideoRepo: PromotedVideoRepo,
    private val youtubeApi: YoutubeApi,
    private val promotedVideoUserLogRepo: PromotedVideoUserLogRepo,
    private val channelSubscriptionRepo: ChannelSubscriptionRepo,
    private val videoGeneratedCommentRepo: VideoGeneratedCommentRepo,
    private val videoRewardConfigRepo: VideoRewardConfigRepo,
) {
    suspend fun getVideoDetails(
        videoId: String,
        includeDeleted: Boolean? = null,
        includeDeactivated: Boolean? = null,
        includeUnavailableVideos:Boolean? = null,
        userId: String
    ): Video {
        val video = videoRepo.findByIdAndIncludeDeletedAndIncludeUnavailableVideo(videoId, includeDeleted, includeDeactivated, includeUnavailableVideos)
            .onEmpty { throw Error.VideoNotFound }
            .first()

        val comments = videoCommentRepo.findAllByVideoIdOrderByIdDesc(videoId, Pageable.ofSize(10)).toList()
        if (comments.isNotEmpty()) {
            val commenterIds = comments.mapTo(mutableSetOf()) { it.commenterId }
            val commenters = userRepo.findAllById(commenterIds).toList()
            comments.forEach { comment ->
                comment.commenter = commenters.first { it.id == comment.commenterId }
            }
        }

        val videoUser = videoUserRepo.findByVideoIdAndUserId(
            videoId = videoId,
            userId = userId
        )
        val isLiked: Boolean? = videoUser?.isLiked

        return video.apply {
            channel = videoChannelRepo.findById(channelId)!!
            category = videoCategoryRepo.findById(categoryId)!!
            this.comments = comments

        }.toVideo(isLiked)
    }

    suspend fun getVideoAnalytics(
        page: Int,
        pageSize: Int
    ): VideoAnalytics {
        val videos = videoRepo.findAllBy(
            limit = pageSize,
            offset = (page - 1) * pageSize
        ).toList()

        val total = videoRepo.countAllBy()

        //Map<video_id, rewarded_points>
        val rewardedPointMap: Map<String, Long> =
            videoRewardConfigRepo.findAllByVideoIdIn(
                videoIds = videos.map { it.id }.toSet()
            ).toList().associate { it.videoId to it.rewardedPoints }

        val trackInfos = videos.map { video ->
            val rewardedPoint = rewardedPointMap[video.id] ?: 0
            video.toTrackInfo(rewardedPoint)
        }

        return VideoAnalytics(
            trackInfos = trackInfos,
            numOfVideos = total
        )
    }

    suspend fun getVideos(
        page: Int,
        pageSize: Int,
        query: String? = null,
        isHighlighted: Boolean? = null,
        categoryId: String? = null,
        userId: String? = null,
        isExcludeIgnoredVideos: Boolean? = null,
        includeDeleted: Boolean? = null,
        includeDeactivated: Boolean? = null,
        includeUnavailableVideos: Boolean? = null,
    ): Pair<List<Video>, Long> {
        val videos = videoRepo.findAllBy(
            isHighlighted = isHighlighted,
            categoryId = categoryId,
            query = query,
            userId = userId,
            isExcludeIgnoredVideos = isExcludeIgnoredVideos,
            includeDeleted = includeDeleted,
            includeDeactivated = includeDeactivated,
            includeUnavailableVideos = includeUnavailableVideos,
            offset = (page - 1) * pageSize,
            limit = pageSize,
        ).toList()
        val total = videoRepo.countAllBy(
            isHighlighted = isHighlighted,
            categoryId = categoryId,
            query = query,
            userId = userId,
            isExcludeIgnoredVideos = isExcludeIgnoredVideos,
            includeDeleted = includeDeleted,
            includeDeactivated = includeDeactivated,
            includeUnavailableVideos = includeUnavailableVideos,
        )
        val channels = videoChannelRepo.findAllById(videos.map { it.channelId }.toSet()).toList()
        val categories = videoCategoryRepo.findAllById(videos.map { it.categoryId }.toSet()).toList()
        return Pair(videos.map { videoModel ->
            videoModel.channel = channels.first { it.id == videoModel.channelId }
            videoModel.category = categories.first { it.id == videoModel.categoryId }
            videoModel.toVideo()
        }, total)
    }

    suspend fun getVideosByIds(
        ids: List<String>,
        userId: String? = null,
        isExcludeIgnoredVideos: Boolean? = null,
        includeDeleted: Boolean? = null,
        includeDeactivated: Boolean? = null,
        includeUnavailableVideos: Boolean? = null,
    ): List<Video> {
        val videos = videoRepo.findByIds(
            ids = ids,
            userId = userId,
            isExcludeIgnoredVideos = isExcludeIgnoredVideos,
            includeDeleted = includeDeleted,
            includeDeactivated = includeDeactivated,
            includeUnavailableVideos = includeUnavailableVideos,
        ).toList()
        val channels = videoChannelRepo.findAllById(videos.map { it.channelId }.toSet()).toList()
        val categories = videoCategoryRepo.findAllById(videos.map { it.categoryId }.toSet()).toList()
        return videos.map { videoModel ->
            videoModel.channel = channels.first { it.id == videoModel.channelId }
            videoModel.category = categories.first { it.id == videoModel.categoryId }
            videoModel.toVideo()
        }
    }

    suspend fun deleteVideo(
        id: String,
    ) {
        videoRepo.softDeleteById(id)
    }

    suspend fun upsertVideo(
        youtubeUrl: String,
        isHighlight: Boolean,
        availableFromArg: Instant? = null,
        availableToArg: Instant? = null,
        youtubeVideo: YoutubeVideo,
        youtubeChannel: YoutubeChannel,
        youtubeCategory: YoutubeVideoCategory
    ): Video {
        val channel = videoChannelRepo.save(
            (videoChannelRepo.findById(youtubeChannel.id)
                ?: VideoChannelModel(youtubeChannel.id).apply { this.isNewRecord = true })
                .apply {
                    title = youtubeChannel.snippet.title
                    thumbnail = youtubeChannel.snippet.thumbnails.standard?.url ?: ""
                }
        )
        val category = videoCategoryRepo.save(
            (videoCategoryRepo.findById(youtubeCategory.id)
                ?: VideoCategoryModel(youtubeCategory.id).apply { this.isNewRecord = true })
                .apply {
                    title = youtubeCategory.snippet.title
                }
        )
        var isNewModel = false
        return videoRepo.save(
            ((videoRepo.findById(youtubeVideo.id) ?: VideoModel(
                _id = youtubeVideo.id,
            ).apply { isNewRecord = true; isNewModel = true }).apply {
                val parsedDuration = Duration.parse(youtubeVideo.contentDetails?.duration)
                title = youtubeVideo.snippet.title
                url = youtubeUrl
                publishedAt = youtubeVideo.snippet.publishedAt
                thumbnail = youtubeVideo.snippet.thumbnails.standard?.url ?: ""
                favoriteCount = youtubeVideo.statistics.favoriteCount.toLong()
                likeCount = youtubeVideo.statistics.likeCount.toLong()
                commentCount = youtubeVideo.statistics.commentCount.toLong()
                viewCount = youtubeVideo.statistics.viewCount.toLong()
                channelId = youtubeChannel.id
                categoryId = youtubeCategory.id
                duration = parsedDuration.toString()
                durationMs = parsedDuration.toMillis()
                isHighlighted = isHighlight
                availableFrom = availableFromArg
                availableTo = availableToArg
            })
        )
            .also {
                if (isNewModel) {
                    videoViewCountRepo.save(
                        VideoViewCountModel(
                            videoId = youtubeVideo.id,
                            viewCount = 0,
                        )
                    )
                }
            }
            .apply {
                this.channel = channel
                this.category = category
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
        videoRepo.addViewCount(videoId)
    }

    suspend fun getTrendingVideos(
        days: Int = 1,
        limit: Int = 10,
        userId: String? = null,
        isExcludeIgnoredVideos: Boolean? = null,
        includeDeleted: Boolean? = null,
        includeDeactivated: Boolean? = null,
        includeUnavailableVideos: Boolean? = null,
    ): List<Video> {
        val trendingVideos = videoViewCountRepo.findTopTrendingVideos(
            days = days,
            limit = limit,
            userId = userId,
            isExcludeIgnoredVideos = isExcludeIgnoredVideos,
            includeDeleted = includeDeleted,
            includeDeactivated = includeDeactivated,
            includeUnavailableVideos = includeUnavailableVideos,
        ).toList().toMutableList()
        if (trendingVideos.size < limit) {
            trendingVideos.addAll(
                videoRepo.findAllByIdNotIn(
                    ids = trendingVideos.takeIf { it.isNotEmpty() }?.map { it.id },
                    limit = limit - trendingVideos.size,
                    userId = userId,
                    isExcludeIgnoredVideos = isExcludeIgnoredVideos,
                    includeDeleted = includeDeleted,
                    includeDeactivated = includeDeactivated,
                    includeUnavailableVideos = includeUnavailableVideos,
                ).toList()
            )
        }
        return trendingVideos.map {
            it.channel = videoChannelRepo.findById(it.channelId)!!
            it.category = videoCategoryRepo.findById(it.categoryId)!!
            it.toVideo()
        }
    }

    suspend fun ignoreVideo(
        videoId: String,
        userId: String
    ) {
        val videoUser = videoUserRepo.findByVideoIdAndUserId(
            videoId = videoId,
            userId = userId
        ) ?: videoUserRepo.save(VideoUserModel(videoId = videoId, userId = userId, isIgnored = true))

        videoUser.isIgnored = true
        videoUserRepo.save(videoUser)
    }

    suspend fun likeVideo(
        videoId: String,
        userId: String,
        action: RateVideoAction,
        youtubeToken: String
    ) {
        if (!videoRepo.existsById(videoId)) throw Error.VideoNotFound

        val isLike = action == RateVideoAction.like
        val cachedRecord = videoUserRepo.findByVideoIdAndUserId(
            videoId = videoId,
            userId = userId,
        )
        if (cachedRecord?.isLiked == true && isLike || cachedRecord?.isLiked == false && !isLike) {
            return
        }

        youtubeApi.rateVideo(videoId, youtubeToken, action.toString())
        videoUserRepo.save(cachedRecord?.apply { isLiked = isLike }
            ?: VideoUserModel(
                videoId = videoId,
                userId = userId,
                isLiked = isLike,
            )
        )
        if (isLike) videoRepo.addLikeCount(videoId)
        else videoRepo.deductLikeCount(videoId)
    }

    suspend fun postComment(
        token: String,
        comment: String,
        commenterId: String,
        videoId: String,
    ): VideoComment {
        if (!videoRepo.existsById(videoId)) throw Error.VideoNotFound
        val commenter = userRepo.findById(commenterId) ?: throw UserNotFound
        youtubeApi.addToplevelComment(videoId, token, comment)
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

    suspend fun getPromotedVideos(
        page: Int,
        pageSize: Int,
        userId: String? = null,
        isExcludeIgnoredVideos: Boolean? = null,
        includeDeleted: Boolean? = null,
        includeDeactivated: Boolean? = null,
        includeUnavailableVideos: Boolean? = null,
    ): Pair<List<PromotedVideoConfig>, Long> {
        val configs = promotedVideoRepo.findAllBy(
            includeDeleted,
            includeDeactivated = includeDeactivated,
            includeUnavailableVideos,
            pageable = Pageable.ofSize(pageSize).withPage(page - 1)
        ).toList()
        val res = promotedVideoRepo.findAllBy(
            offset = (page - 1) * pageSize,
            limit = pageSize,
            userId = userId,
            isExcludeIgnoredVideos = isExcludeIgnoredVideos,
            includeDeleted = includeDeleted,
            includeDeactivated = includeDeactivated,
            includeUnavailableVideos = includeUnavailableVideos,
        ).toList().map {
            it.channel = videoChannelRepo.findById(it.channelId)!!
            it.category = videoCategoryRepo.findById(it.categoryId)!!
            it.toVideo()
        }
        val count = promotedVideoRepo.countAllBy(
            userId = userId,
            isExcludeIgnoredVideos = isExcludeIgnoredVideos,
            includeDeleted = includeDeleted,
            includeDeactivated = includeDeactivated,
            includeUnavailableVideos = includeUnavailableVideos,
        )

        return Pair(configs.map { config -> config.toPromotedVideo(res.firstOrNull() { it.id == config.videoId }) }, count)
    }

    suspend fun promoteVideo(
        videoId: String,
        payload: PromoteVideoRequest
    ) {
        // Currently we only allow 1 video to be promoted
        promotedVideoRepo.deleteAll()
        promotedVideoRepo.save(
            PromotedVideoModel(
                videoId = videoId,
                startDate = payload.startDate,
                endDate = payload.endDate,
                autoLike = payload.autoLike,
                autoComment = payload.autoComment,
                autoPlay = payload.autoPlay,
                autoSubscribe = payload.autoSubscribe,
                interactionDelay = payload.interactionDelay
            )
        )
    }

    suspend fun upsertGeneratedComments(
        videoId: String,
        comments: List<String>
    ) {
        videoGeneratedCommentRepo.deleteAllByVideoId(videoId)
        videoGeneratedCommentRepo.saveAll(
            comments.map {
                VideoGeneratedComment(
                    content = it,
                    videoId = videoId,
                )
            }
        ).toList()
    }

    suspend fun reactToPromotedVideo(
        videoId: String, youtubeToken: String, userId: String
    ) {
        val configs = promotedVideoRepo.findAllBy(
            includeDeleted = false,
            pageable = Pageable.ofSize(10).withPage(0)
        ).toList()
        if (configs.isEmpty()) throw CustomBadRequestException("Invalid promoted videoId")
        val config = configs[0];
        if (config.videoId != videoId) throw CustomBadRequestException("Invalid promoted videoId")
        val record = promotedVideoUserLogRepo.findByVideoIdAndUserId(videoId, config.videoId)
        if (record != null && record.liked && record.commented && record.subscribed) return
        val videoDetail = videoRepo.findById(videoId)!!
        //TODO: update the comment
        val newRecord = PromotedVideoUserLogModel(
            userId = userId,
            videoId = videoId,
            channelId = videoDetail.channelId,
            liked = record?.liked ?: false,
            commented = record?.commented ?: false,
            subscribed = record?.subscribed ?: false
        )
        if (config.autoLike && !newRecord.liked) {
            try {
                val likedVideoRecord = videoUserRepo.findByVideoIdAndUserId(
                    videoId = videoId,
                    userId = userId,
                )
                if (likedVideoRecord == null || !likedVideoRecord.isLiked) {
                    youtubeApi.rateVideo(videoId, youtubeToken, RateVideoAction.like.toString())
                    videoUserRepo.save(likedVideoRecord?.apply {
                        isLiked = true
                    } ?: VideoUserModel(
                        videoId = videoId,
                        userId = userId,
                        isLiked = true
                    ))
                }
                newRecord.liked = true
            } catch (e: Exception) {
                print("Unable to like video: ${e.message}")
            }
        }
        val comments = videoGeneratedCommentRepo.findAllByVideoId(videoId).toList()
        if (config.autoComment && !newRecord.commented && comments.isNotEmpty()) {
            try {
                val commentContent = comments.random().content
                youtubeApi.addToplevelComment(videoId, youtubeToken, commentContent)
                videoCommentRepo.save(
                    VideoCommentModel(
                        comment = commentContent,
                        commenterId = userId,
                        videoId = videoId,
                    )
                )
                newRecord.commented = true
            } catch (e: Exception) {
                print("Unable to comment on video: ${e.message}")
            }
        }

        if (config.autoSubscribe && !newRecord.subscribed) {
            val existingSubscription = channelSubscriptionRepo.findByChannelIdAndUserId(newRecord.channelId, userId)
            if (existingSubscription == null) {
                try {
                    val youtubeResource = youtubeApi.subscribeToChannel(videoDetail.channelId, youtubeToken)
                    channelSubscriptionRepo.save(
                        ChannelSubscriptionModel(
                            userId = userId,
                            channelId = newRecord.channelId,
                            subscriptionId = youtubeResource.id,
                        )
                    )
                    newRecord.subscribed = true
                } catch (e: Exception) {
                    print("Unable to subscribe to channel: ${e.message}")
                }
            } else {
                newRecord.subscribed = true
            }
        }
        promotedVideoUserLogRepo.save(newRecord)
    }
}