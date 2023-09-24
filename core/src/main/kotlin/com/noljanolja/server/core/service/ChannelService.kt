package com.noljanolja.server.core.service

import com.noljanolja.server.common.exception.BaseException
import com.noljanolja.server.core.model.Channel
import com.noljanolja.server.core.repo.media.ChannelSubscriptionModel
import com.noljanolja.server.core.repo.media.ChannelSubscriptionRepo
import com.noljanolja.server.core.repo.media.VideoChannelRepo
import com.noljanolja.server.core.repo.media.toVideoChannel
import com.noljanolja.server.youtube.service.YoutubeApi
import org.springframework.stereotype.Component

@Component
class ChannelService(
    private val videoChannelRepo: VideoChannelRepo,
    private val channelSubscriptionRepo: ChannelSubscriptionRepo,
    private val youtubeApi: YoutubeApi
) {
    suspend fun getChannelDetail(channelId: String) : Channel? {
        return videoChannelRepo.findById(channelId)?.toVideoChannel()
    }

    suspend fun getSubscriptionInfo(channelId: String, userId: String): ChannelSubscriptionModel? {
        return channelSubscriptionRepo.findByChannelIdAndUserId(channelId, userId)
    }

    suspend fun addSubscription(channelId: String, userId: String, token: String) {
        val existingRecord = getSubscriptionInfo(channelId, userId)
        if (existingRecord == null) {
            val youtubeResource = youtubeApi.subscribeToChannel(channelId, token)
            channelSubscriptionRepo.save(ChannelSubscriptionModel(
                userId = userId, channelId = channelId, subscriptionId = youtubeResource.id
            ))
        }
    }

    suspend fun removeSubscription(channelId: String, userId: String, token: String) {
        val existingRecord = getSubscriptionInfo(channelId, userId)
            ?: throw BaseException(400, "No subscriptionId to be removed", null)
        youtubeApi.unsubscribeFromChannel(existingRecord.subscriptionId, token)
        channelSubscriptionRepo.unsubscribeUserFromChannel(channelId, userId)
    }
}