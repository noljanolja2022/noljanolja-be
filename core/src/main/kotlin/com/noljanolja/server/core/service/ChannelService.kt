package com.noljanolja.server.core.service

import com.noljanolja.server.core.model.Channel
import com.noljanolja.server.core.repo.media.ChannelSubscriptionModel
import com.noljanolja.server.core.repo.media.ChannelSubscriptionRepo
import com.noljanolja.server.core.repo.media.VideoChannelRepo
import com.noljanolja.server.core.repo.media.toVideoChannel
import org.springframework.stereotype.Component

@Component
class ChannelService(
    private val videoChannelRepo: VideoChannelRepo,
    private val channelSubscriptionRepo: ChannelSubscriptionRepo
) {
    suspend fun getChannelDetail(channelId: String) : Channel? {
        return videoChannelRepo.findById(channelId)?.toVideoChannel()
    }

    suspend fun getSubscriptionInfo(channelId: String, userId: String): ChannelSubscriptionModel? {
        return channelSubscriptionRepo.findByChannelIdAndUserId(channelId, userId)
    }

    suspend fun addSubscription(channelId: String, userId: String, subscriptionId: String) {
        val existingRecord = getSubscriptionInfo(channelId, userId)
        if (existingRecord == null) {
            channelSubscriptionRepo.save(ChannelSubscriptionModel(
                userId = userId, channelId = channelId, subscriptionId = subscriptionId
            ))
        }
    }

    suspend fun removeSubscription(channelId: String, userId: String) {
        channelSubscriptionRepo.unsubscribeUserFromChannel(channelId, userId)
    }
}