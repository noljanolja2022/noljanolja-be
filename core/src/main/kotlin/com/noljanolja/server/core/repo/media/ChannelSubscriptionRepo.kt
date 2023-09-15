package com.noljanolja.server.core.repo.media

import org.springframework.data.r2dbc.repository.Modifying
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ChannelSubscriptionRepo : CoroutineCrudRepository<ChannelSubscriptionModel, Long> {

    suspend fun findByChannelIdAndUserId(
        channelId: String, userId: String
    ): ChannelSubscriptionModel?

    @Modifying
    @Query(
        """
        DELETE FROM channel_subscription_records 
        WHERE channel_id = :channelId AND user_id = :userId
    """
    )
    fun unsubscribeUserFromChannel(channelId: String, userId: String)
}