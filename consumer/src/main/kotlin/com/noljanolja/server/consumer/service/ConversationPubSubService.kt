package com.noljanolja.server.consumer.service

import com.noljanolja.server.consumer.model.Conversation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.listener.ChannelTopic
import org.springframework.data.redis.listener.ReactiveRedisMessageListenerContainer
import org.springframework.stereotype.Service


@Service
class ConversationPubSubService(
    private val reactiveTemplate: ReactiveRedisTemplate<String, Conversation>,
    private val reactiveMsgListenerContainer: ReactiveRedisMessageListenerContainer
) {

    companion object {
        fun getTopic(userId: String) = "conversations-$userId"
    }
    fun publish(
        topic: String,
        conversation: Conversation,
    ) {
        reactiveTemplate
            .convertAndSend(topic, conversation)
            .subscribe()
    }

    fun subscribe(
        topic: String,
    ): Flow<Conversation> = reactiveMsgListenerContainer
        .receive(
            listOf(ChannelTopic(topic)),
            reactiveTemplate.serializationContext.keySerializationPair,
            reactiveTemplate.serializationContext.valueSerializationPair
        )
        .map { it.message }
        .asFlow()
}
