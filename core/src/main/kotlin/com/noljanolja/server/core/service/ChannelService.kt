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

    suspend fun subscribeUserToChannel(channelId: String, userId: String, isSubscribing: Boolean, subscriptionId: String? = null): String? {
        val existingRecord = channelSubscriptionRepo.findByChannelIdAndUserId(channelId, userId)
        if (isSubscribing && subscriptionId != null) {
            if (existingRecord == null) {
                channelSubscriptionRepo.save(ChannelSubscriptionModel(
                    userId = userId, channelId = channelId, subscriptionId = subscriptionId
                ))
            }
        } else if (!isSubscribing && existingRecord != null) {
            channelSubscriptionRepo.unsubscribeUserFromChannel(channelId, userId)
        }
        return existingRecord?.subscriptionId ?: subscriptionId
    }
}